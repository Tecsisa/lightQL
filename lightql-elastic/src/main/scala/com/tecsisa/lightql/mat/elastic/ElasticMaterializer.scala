/*
 * Copyright (C) 2016 - 2018 TECNOLOGIA, SISTEMAS Y APLICACIONES S.L. <http://www.tecsisa.com>
 */

package com.tecsisa.lightql
package mat
package elastic

import com.sksamuel.elastic4s.searches.queries.matches.MatchQuery
import com.sksamuel.elastic4s.searches.queries.term.{ BuildableTermsQuery, TermQuery, TermsQuery }
import com.sksamuel.elastic4s.searches.queries.{ BoolQuery, NestedQuery, Query => EsQuery, RangeQuery }
import com.tecsisa.lightql.ast.ClauseTree.{ Clause, CombinedClause }
import com.tecsisa.lightql.ast.LogicOperator.{ and, or }
import com.tecsisa.lightql.ast.{ ClauseTree, LogicOperator, Query, EqualityOperator => EqOp, MatchingOperator => MatchOp, NumericOperator => NumOp }

trait ElasticMaterializer extends Materializer[EsQuery] {

  protected implicit def btq: BuildableTermsQuery[AnyRef]

  def materialize(query: Query): EsQuery = {

    def loop(ct: ClauseTree, qb: BoolQuery): EsQuery = {

      def buildTermQueryFromClause[V](field: ClauseTree.Field, value: V): EsQuery =
        value match {
          case seq: Iterable[AnyRef] @unchecked =>
            TermsQuery(stdField(field), seq)
          case _ => TermQuery(stdField(field), value)
        }

      def buildQueryFromClause[V](c: Clause[V], qb: BoolQuery): BoolQuery =
        c.op match {
          case EqOp.`=` =>
            qb.must(nestQuery(buildTermQueryFromClause(c.field, c.value), c.field))
          case EqOp.!= =>
            qb.not(nestQuery(buildTermQueryFromClause(c.field, c.value), c.field))
          case MatchOp.~ =>
            qb.must(nestQuery(MatchQuery(stdField(c.field), c.value), c.field))
          case MatchOp.!~ =>
            qb.not(nestQuery(MatchQuery(stdField(c.field), c.value), c.field))
          case NumOp.< =>
            qb.filter(
              nestQuery(RangeQuery(stdField(c.field)).lt(c.value.toString), c.field)
            )
          case NumOp.<= =>
            qb.filter(
              nestQuery(RangeQuery(stdField(c.field)).lte(c.value.toString), c.field)
            )
          case NumOp.> =>
            qb.filter(
              nestQuery(RangeQuery(stdField(c.field)).gt(c.value.toString), c.field)
            )
          case NumOp.>= =>
            qb.filter(
              nestQuery(RangeQuery(stdField(c.field)).gte(c.value.toString), c.field)
            )
          case _ => qb
        }

      def buildQueryFromCombinedAndClause[V](
          qb: BoolQuery,
          lop: LogicOperator,
          ct: ClauseTree,
          c: Clause[V]
      ) = lop match {
        case `and` =>
          qb.must(Seq(loop(ct, BoolQuery()), buildQueryFromClause(c, qb)))
        case `or` =>
          qb.should(Seq(loop(ct, qb), buildQueryFromClause(c, qb)))
      }

      ct match {
        case Clause(field, op, value) =>
          val query = op match {
            case EqOp.`=`   => buildTermQueryFromClause(field, value)
            case EqOp.!=    => qb.not(buildTermQueryFromClause(field, value))
            case MatchOp.~  => MatchQuery(stdField(field), value)
            case MatchOp.!~ => qb.not(MatchQuery(stdField(field), value))
            case NumOp.< =>
              RangeQuery(stdField(field)).lt(value.toString)
            case NumOp.<= => RangeQuery(stdField(field)).lte(value.toString)
            case NumOp.> =>
              RangeQuery(stdField(field)).gt(value.toString)
            case NumOp.>= => RangeQuery(stdField(field)).gte(value.toString)
            case _        => sys.error("Impossible")
          }
          if (isNested(field))
            NestedQuery(fieldPath(field), query)
          else query
        case CombinedClause(lct, lop, rct) =>
          (lct, rct) match {
            case (_: CombinedClause, _: CombinedClause) =>
              def f(q: EsQuery) = lop match {
                case `and` => qb.must(q); case `or` => qb.should(q)
              }
              qb.filter(Seq(lct, rct).map(x => f(loop(x, BoolQuery()))))
            case (_: CombinedClause, c: Clause[_]) =>
              buildQueryFromCombinedAndClause(qb, lop, lct, c)
            case (c: Clause[_], _: CombinedClause) =>
              buildQueryFromCombinedAndClause(qb, lop, rct, c)
            case (cl: Clause[_], cr: Clause[_]) =>
              lop match {
                case `and` =>
                  qb.must(Seq(cl, cr).map(buildQueryFromClause(_, qb)))
                case `or` =>
                  qb.should(Seq(cl, cr).map(c => qb.should(loop(c, qb))))
              }
          } // combined clause pattern matching
      } // main pattern matching
    } // loop
    loop(query.ct, BoolQuery())
  } // Materializer

  private[this] def stdField(field: ClauseTree.Field): ClauseTree.Field =
    field.replace("->", ".")

  private[this] def isNested(field: ClauseTree.Field): Boolean = field.contains("->")

  private[this] def fieldPath(field: ClauseTree.Field): String =
    stdField(field.dropRight(field.split("->").last.length + 2))

  private[this] def nestQuery(qb: EsQuery, field: ClauseTree.Field): EsQuery =
    if (isNested(field)) NestedQuery(fieldPath(field), qb) else qb

}

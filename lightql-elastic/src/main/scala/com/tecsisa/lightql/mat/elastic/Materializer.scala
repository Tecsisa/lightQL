/*
 * Copyright (C) 2016, 2017 TECNOLOGIA, SISTEMAS Y APLICACIONES S.L. <http://www.tecsisa.com>
 */
package com.tecsisa.lightql
package mat
package elastic

import com.sksamuel.elastic4s.searches.queries.BuildableTermsQueryImplicits
import com.sksamuel.elastic4s.searches.queries.{ BoolQueryDefinition, NestedQueryDefinition }
import com.sksamuel.elastic4s.searches.queries.{ QueryDefinition, RangeQueryDefinition }
import com.sksamuel.elastic4s.searches.queries.matches.MatchQueryDefinition
import com.sksamuel.elastic4s.searches.queries.term.{ TermQueryDefinition, TermsQueryDefinition }
import com.tecsisa.lightql.ast.ClauseTree.{ Clause, CombinedClause }
import com.tecsisa.lightql.ast.{ ClauseTree, Query }
import com.tecsisa.lightql.ast.{ EqualityOperator => EqOp }
import com.tecsisa.lightql.ast.{ NumericOperator => NumOp }
import com.tecsisa.lightql.ast.{ MatchingOperator => MatchOp }
import com.tecsisa.lightql.ast.LogicOperator
import com.tecsisa.lightql.ast.LogicOperator.{ and, or }

trait Materializer[T] {
  def materialize(query: Query): T
}

object Materializer extends BuildableTermsQueryImplicits {

  implicit val elasticMaterializer: Materializer[QueryDefinition] =
    new Materializer[QueryDefinition] {
      def materialize(query: Query): QueryDefinition = {

        def loop(ct: ClauseTree, qb: BoolQueryDefinition): QueryDefinition = {

          def buildTermQueryFromClause[V](field: ClauseTree.Field, value: V): QueryDefinition =
            value match {
              case seq: Iterable[AnyRef] @unchecked =>
                TermsQueryDefinition(stdField(field), seq)
              case _ => TermQueryDefinition(stdField(field), value)
            }

          def buildQueryFromClause[V](c: Clause[V], qb: BoolQueryDefinition): BoolQueryDefinition =
            c.op match {
              case EqOp.`=` =>
                qb.must(nestQuery(buildTermQueryFromClause(c.field, c.value), c.field))
              case EqOp.!= =>
                qb.not(nestQuery(buildTermQueryFromClause(c.field, c.value), c.field))
              case MatchOp.~ =>
                qb.must(nestQuery(MatchQueryDefinition(stdField(c.field), c.value), c.field))
              case MatchOp.!~ =>
                qb.not(nestQuery(MatchQueryDefinition(stdField(c.field), c.value), c.field))
              case NumOp.< =>
                qb.filter(
                  nestQuery(RangeQueryDefinition(stdField(c.field)).lt(c.value.toString), c.field)
                )
              case NumOp.<= =>
                qb.filter(
                  nestQuery(RangeQueryDefinition(stdField(c.field)).lte(c.value.toString), c.field)
                )
              case NumOp.> =>
                qb.filter(
                  nestQuery(RangeQueryDefinition(stdField(c.field)).gt(c.value.toString), c.field)
                )
              case NumOp.>= =>
                qb.filter(
                  nestQuery(RangeQueryDefinition(stdField(c.field)).gte(c.value.toString), c.field)
                )
              case _ => qb
            }

          def buildQueryFromCombinedAndClause[V](
            qb: BoolQueryDefinition,
            lop: LogicOperator,
            ct: ClauseTree,
            c: Clause[V]
          ) = lop match {
            case `and` =>
              qb.must(Seq(loop(ct, BoolQueryDefinition()), buildQueryFromClause(c, qb)))
            case `or` =>
              qb.should(Seq(loop(ct, qb), buildQueryFromClause(c, qb)))
          }

          ct match {
            case Clause(field, op, value) =>
              val query = op match {
                case EqOp.`=`   => buildTermQueryFromClause(field, value)
                case EqOp.!=    => qb.not(buildTermQueryFromClause(field, value))
                case MatchOp.~  => MatchQueryDefinition(stdField(field), value)
                case MatchOp.!~ => qb.not(MatchQueryDefinition(stdField(field), value))
                case NumOp.< =>
                  RangeQueryDefinition(stdField(field)).lt(value.toString)
                case NumOp.<= => RangeQueryDefinition(stdField(field)).lte(value.toString)
                case NumOp.> =>
                  RangeQueryDefinition(stdField(field)).gt(value.toString)
                case NumOp.>= => RangeQueryDefinition(stdField(field)).gte(value.toString)
                case _        => sys.error("Impossible")
              }
              if (isNested(field))
                NestedQueryDefinition(fieldPath(field), query)
              else query
            case CombinedClause(lct, lop, rct) =>
              (lct, rct) match {
                case (_: CombinedClause, _: CombinedClause) =>
                  def f(q: QueryDefinition) = lop match {
                    case `and` => qb.must(q); case `or` => qb.should(q)
                  }
                  qb.filter(Seq(lct, rct).map(x => f(loop(x, BoolQueryDefinition()))))
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
        loop(query.ct, BoolQueryDefinition())
      }

      private[this] def stdField(field: ClauseTree.Field): ClauseTree.Field =
        field.replace("->", ".")

      private[this] def isNested(field: ClauseTree.Field): Boolean = field.contains("->")

      private[this] def fieldPath(field: ClauseTree.Field): String =
        stdField(field.dropRight(field.split("->").last.length + 2))

      private[this] def nestQuery(qb: QueryDefinition, field: ClauseTree.Field): QueryDefinition =
        if (isNested(field)) NestedQueryDefinition(fieldPath(field), qb) else qb
    } // Materializer
}

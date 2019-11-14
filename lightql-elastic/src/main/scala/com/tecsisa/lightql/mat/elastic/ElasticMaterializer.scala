/*
 * Copyright (C) 2016 - 2018 TECNOLOGIA, SISTEMAS Y APLICACIONES S.L. <http://www.tecsisa.com>
 */

package com.tecsisa.lightql
package mat
package elastic

import com.sksamuel.elastic4s.requests.searches.queries.matches.MatchQuery
import com.sksamuel.elastic4s.requests.searches.queries.term.{ TermQuery, TermsQuery }
import com.sksamuel.elastic4s.requests.searches.queries.{ BoolQuery, NestedQuery, RangeQuery, Query => EsQuery }
import com.tecsisa.lightql.ast.ClauseTree.{ Clause, CombinedClause, Field, Nested }
import com.tecsisa.lightql.ast.LogicOperator.{ and, or }
import com.tecsisa.lightql.ast.{ ClauseTree, Query, EqualityOperator => EqOp, MatchingOperator => MatchOp, NumericOperator => NumOp }

trait ElasticMaterializer extends Materializer[EsQuery] {

  def materialize(query: Query): EsQuery = {

    def loop(ct: ClauseTree, qb: BoolQuery): EsQuery = {

      def buildTermQueryFromClause[V](field: ClauseTree.Field, value: V): EsQuery =
        value match {
          case seq: Iterable[AnyRef] @unchecked =>
            TermsQuery(field, seq)
          case _ => TermQuery(field, value)
        }

      def prependPathToFields(ct: ClauseTree, path: Field): ClauseTree =
        ct match {
          case Nested(field, tree)      => Nested(path + "." + field, prependPathToFields(tree, path))
          case Clause(field, op, value) => Clause(path + "." + field, op, value)
          case CombinedClause(lct, op, rct) =>
            CombinedClause(prependPathToFields(lct, path), op, prependPathToFields(rct, path))
        }

      ct match {
        case Nested(path, tree) =>
          NestedQuery(path, loop(prependPathToFields(tree, path), qb))
        case Clause(field, op, value) =>
          op match {
            case EqOp.`=` =>
              buildTermQueryFromClause(field, value)
            case EqOp.!= =>
              qb.not(buildTermQueryFromClause(field, value))
            case MatchOp.~ =>
              MatchQuery(field, value)
            case MatchOp.!~ =>
              qb.not(MatchQuery(field, value))
            case NumOp.< =>
              RangeQuery(field).lt(value.toString)
            case NumOp.<= =>
              RangeQuery(field).lte(value.toString)
            case NumOp.> =>
              RangeQuery(field).gt(value.toString)
            case NumOp.>= =>
              RangeQuery(field).gte(value.toString)
            case _ =>
              sys.error("Impossible")
          }
        case CombinedClause(lct, lop, rct) =>
          lop match {
            case `and` =>
              qb.must(Seq(loop(lct, BoolQuery()), loop(rct, BoolQuery())))
            case `or` =>
              qb.should(Seq(loop(lct, qb), loop(rct, qb)))
          }
      } // main pattern matching
    } // loop
    loop(query.ct, BoolQuery())
  } // Materializer

}

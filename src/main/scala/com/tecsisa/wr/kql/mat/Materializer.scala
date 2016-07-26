package com.tecsisa.wr
package kql
package mat

import com.tecsisa.wr.kql.ast.ClauseTree.{ Clause, CombinedClause }
import com.tecsisa.wr.kql.ast.LogicOperator.{ and, or }
import com.tecsisa.wr.kql.ast.{ ClauseTree, Query, EqualityOperator => EqOp }
import org.elasticsearch.index.query.QueryBuilders.{ boolQuery, constantScoreQuery, termQuery }
import org.elasticsearch.index.query.{ BoolQueryBuilder, QueryBuilder }

trait Materializer[T] {
  def materialize(query: Query): T
}

object Materializer {
  implicit def elasticMaterializer: Materializer[QueryBuilder] =
    new Materializer[QueryBuilder] {
      def materialize(query: Query): QueryBuilder = {
        def loop(ct: ClauseTree, qb: BoolQueryBuilder): QueryBuilder = {
          ct match {
            case Clause(field, _, value) => termQuery(field, value)
            case CombinedClause(lct, op, rct) =>
              def join(f: QueryBuilder => BoolQueryBuilder) = {
                Seq(lct, rct).foreach(x => coloop(x, qb)(f))
                qb
              }
              op match {
                case `and` => join(qb.must)
                case `or`  => join(qb.should)
              }
          }
        } // loop
        def coloop(ct: ClauseTree, qb: BoolQueryBuilder)(
            f: QueryBuilder => BoolQueryBuilder): QueryBuilder = ct match {
          case Clause(_, op, _) =>
            op match {
              case EqOp.!= => qb.mustNot(loop(ct, qb))
              case _       => f(loop(ct, qb))
            }
          case _: CombinedClause => f(loop(ct, boolQuery()))
        }
        // a `constant_score` query is syntactic sugar for a `bool`
        // query that contains just a filter and no one scoring query
        // @see http://bit.ly/29TzGph
        constantScoreQuery(loop(query.ct, boolQuery()))
      }
    }
}

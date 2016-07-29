package com.tecsisa.wr
package kql
package mat

import com.tecsisa.wr.kql.ast.ClauseTree.{ Clause, CombinedClause }
import com.tecsisa.wr.kql.ast.LogicOperator.{ and, or }
import com.tecsisa.wr.kql.ast.{ ClauseTree, EqualityOperator => EqOp, LogicOperator }
import com.tecsisa.wr.kql.ast.{ Query, MatchingOperator => MatchOp }
import org.elasticsearch.index.query.QueryBuilders.{ boolQuery, matchQuery, termQuery }
import org.elasticsearch.index.query.{ BoolQueryBuilder, QueryBuilder }

trait Materializer[T] {
  def materialize(query: Query): T
}

object Materializer {
  implicit def elasticMaterializer: Materializer[QueryBuilder] =
    new Materializer[QueryBuilder] {
      def materialize(query: Query): QueryBuilder = {
        def loop(ct: ClauseTree, qb: BoolQueryBuilder): QueryBuilder = {

          def buildQueryFromClause[V](c: Clause[V], qb: BoolQueryBuilder): QueryBuilder =
            c.op match {
              case EqOp.`=`  => qb.filter(termQuery(c.field, c.value))
              case EqOp.!=   => qb.mustNot(termQuery(c.field, c.value))
              case MatchOp.~ => qb.must(matchQuery(c.field, c.value))
              case _         => qb.mustNot(matchQuery(c.field, c.value))
            }

          def buildQueryFromCombinedAndClause[V](qb: BoolQueryBuilder,
                                                 lop: LogicOperator,
                                                 ct: ClauseTree,
                                                 c: Clause[V]) = lop match {
            case `and` =>
              qb.must(loop(ct, boolQuery()))
              buildQueryFromClause(c, qb)
            case `or` =>
              qb.should(loop(ct, boolQuery()))
              qb.should(buildQueryFromClause(c, boolQuery()))
          }

          ct match {
            case Clause(field, op, value) =>
              op match {
                case EqOp.`=`  => termQuery(field, value)
                case EqOp.!=   => boolQuery().mustNot(termQuery(field, value))
                case MatchOp.~ => matchQuery(field, value)
                case _         => boolQuery().mustNot(matchQuery(field, value))
              }
            case CombinedClause(lct, lop, rct) =>
              (lct, rct) match {
                case (_: CombinedClause, _: CombinedClause) =>
                  val f = lop match { case `and` => qb.must _; case `or` => qb.should _ }
                  Seq(lct, rct).foreach(x => f(loop(x, boolQuery())))
                  qb
                case (_: CombinedClause, c @ Clause(_, _, _)) =>
                  buildQueryFromCombinedAndClause(qb, lop, lct, c)
                case (c @ Clause(_, _, _), _: CombinedClause) =>
                  buildQueryFromCombinedAndClause(qb, lop, rct, c)
                case (cl @ Clause(_, opl, _), cr @ Clause(_, opr, _)) =>
                  lop match {
                    case `and` =>
                      Seq(cl, cr).foreach(buildQueryFromClause(_, qb))
                      qb
                    case `or` =>
                      Seq(cl, cr).foreach(c => qb.should(loop(c, qb)))
                      qb
                  }
              } // combined clause pattern matching
          } // main pattern matching
        }   // loop
        loop(query.ct, boolQuery())
      }
    } // Materializer
}

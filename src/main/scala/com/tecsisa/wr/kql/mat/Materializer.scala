package com.tecsisa.wr
package kql
package mat

import com.tecsisa.wr.kql.ast.ClauseTree.{Clause, CombinedClause}
import com.tecsisa.wr.kql.ast.LogicOperator.{and, or}
import com.tecsisa.wr.kql.ast.{ClauseTree, LogicOperator, EqualityOperator => EqOp}
import com.tecsisa.wr.kql.ast.{Query, MatchingOperator => MatchOp, NumericOperator => NumOp}
import org.elasticsearch.index.query.QueryBuilders.{boolQuery, termQuery, termsQuery}
import org.elasticsearch.index.query.QueryBuilders.{matchQuery, nestedQuery, rangeQuery}
import org.elasticsearch.index.query.{BoolQueryBuilder, QueryBuilder}
import scala.collection.JavaConverters.seqAsJavaListConverter

trait Materializer[T] {
  def materialize(query: Query): T
}

object Materializer {

  def isNested(field: ClauseTree.Field): Boolean = field.contains(".")

  def fieldPath(field: ClauseTree.Field): String =
    field.dropRight(field.split("\\.").last.length + 1)

  def nestQuery(qb: QueryBuilder, field: ClauseTree.Field): QueryBuilder =
    if (isNested(field)) nestedQuery(fieldPath(field), qb) else qb

  implicit def elasticMaterializer: Materializer[QueryBuilder] =
    new Materializer[QueryBuilder] {
      def materialize(query: Query): QueryBuilder = {
        def loop(ct: ClauseTree, qb: BoolQueryBuilder): QueryBuilder = {

          def buildQueryFromClause[V](c: Clause[V], qb: BoolQueryBuilder): QueryBuilder = {
            c.op match {
              case EqOp.`=`   => qb.filter(nestQuery(termQuery(c.field, c.value), c.field))
              case EqOp.!=    => qb.mustNot(nestQuery(termQuery(c.field, c.value), c.field))
              case MatchOp.~  => qb.must(nestQuery(matchQuery(c.field, c.value), c.field))
              case MatchOp.!~ => qb.mustNot(nestQuery(matchQuery(c.field, c.value), c.field))
              case NumOp.<    => qb.filter(nestQuery(rangeQuery(c.field).lt(c.value), c.field))
              case NumOp.<=   => qb.filter(nestQuery(rangeQuery(c.field).lte(c.value), c.field))
              case NumOp.>    => qb.filter(nestQuery(rangeQuery(c.field).gt(c.value), c.field))
              case NumOp.>=   => qb.filter(nestQuery(rangeQuery(c.field).gte(c.value), c.field))
              case _          => qb
            }
          }

          def buildQueryFromCombinedAndClause[V](
              qb: BoolQueryBuilder,
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
              val query = op match {
                case EqOp.`=` =>
                  value match {
                    case list: List[_] => termsQuery(field, list.asJava)
                    case _             => termQuery(field, value)
                  }
                case EqOp.!= =>
                  value match {
                    case list: List[_] => boolQuery().mustNot(termsQuery(field, list.asJava))
                    case _             => boolQuery().mustNot(termQuery(field, value))
                  }
                case MatchOp.~  => matchQuery(field, value)
                case MatchOp.!~ => boolQuery().mustNot(matchQuery(field, value))
                case NumOp.<    => rangeQuery(field).lt(value)
                case NumOp.<=   => rangeQuery(field).lte(value)
                case NumOp.>    => rangeQuery(field).gt(value)
                case NumOp.>=   => rangeQuery(field).gte(value)
                case _          => sys.error("Impossible")
              }
              isNested(field) match {
                case true =>
                  nestedQuery(fieldPath(field), query)
                case _ => query
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

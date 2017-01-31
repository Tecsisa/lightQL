/*
 * Copyright (C) 2016 TECNOLOGIA, SISTEMAS Y APLICACIONES S.L. <http://www.tecsisa.com>
 */
   package com.tecsisa.lightql
package mat
package elastic

import com.tecsisa.lightql.ast.ClauseTree.{ Clause, CombinedClause }
import com.tecsisa.lightql.ast.LogicOperator.{ and, or }
import com.tecsisa.lightql.ast.{
  ClauseTree,
  LogicOperator,
  Query,
  EqualityOperator => EqOp,
  MatchingOperator => MatchOp,
  NumericOperator => NumOp
}
import org.elasticsearch.index.query.QueryBuilders._
import org.elasticsearch.index.query.{ BoolQueryBuilder, QueryBuilder }

import scala.collection.JavaConverters.seqAsJavaListConverter

trait Materializer[T] {
  def materialize(query: Query): T
}

object Materializer {

  def stdField(field: ClauseTree.Field): ClauseTree.Field = field.replace("->", ".")

  def isNested(field: ClauseTree.Field): Boolean = field.contains("->")

  def fieldPath(field: ClauseTree.Field): String =
    stdField(field.dropRight(field.split("->").last.length + 2))

  def nestQuery(qb: QueryBuilder, field: ClauseTree.Field): QueryBuilder =
    if (isNested(field)) nestedQuery(fieldPath(field), qb) else qb

  implicit def elasticMaterializer: Materializer[QueryBuilder] =
    new Materializer[QueryBuilder] {
      def materialize(query: Query): QueryBuilder = {
        def loop(ct: ClauseTree, qb: BoolQueryBuilder): QueryBuilder = {

          def buildTermQueryFromClause[V](field: ClauseTree.Field, value: V): QueryBuilder =
            value match {
              case list: List[_] => termsQuery(stdField(field), list.asJava)
              case _             => termQuery(stdField(field), value)
            }

          def buildQueryFromClause[V](c: Clause[V], qb: BoolQueryBuilder): QueryBuilder =
            c.op match {
              case EqOp.`=` =>
                qb.must(nestQuery(buildTermQueryFromClause(c.field, c.value), c.field))
              case EqOp.!= =>
                qb.mustNot(nestQuery(buildTermQueryFromClause(c.field, c.value), c.field))
              case MatchOp.~  => qb.must(nestQuery(matchQuery(stdField(c.field), c.value), c.field))
              case MatchOp.!~ => qb.mustNot(nestQuery(matchQuery(stdField(c.field), c.value), c.field))
              case NumOp.<    => qb.filter(nestQuery(rangeQuery(stdField(c.field)).lt(c.value), c.field))
              case NumOp.<=   => qb.filter(nestQuery(rangeQuery(stdField(c.field)).lte(c.value), c.field))
              case NumOp.>    => qb.filter(nestQuery(rangeQuery(stdField(c.field)).gt(c.value), c.field))
              case NumOp.>=   => qb.filter(nestQuery(rangeQuery(stdField(c.field)).gte(c.value), c.field))
              case _          => qb
            }

          def buildQueryFromCombinedAndClause[V](
              qb: BoolQueryBuilder,
              lop: LogicOperator,
              ct: ClauseTree,
              c: Clause[V]
          ) = lop match {
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
                case EqOp.`=`   => buildTermQueryFromClause(field, value)
                case EqOp.!=    => boolQuery().mustNot(buildTermQueryFromClause(field, value))
                case MatchOp.~  => matchQuery(stdField(field), value)
                case MatchOp.!~ => boolQuery().mustNot(matchQuery(stdField(field), value))
                case NumOp.<    => rangeQuery(stdField(field)).lt(value)
                case NumOp.<=   => rangeQuery(stdField(field)).lte(value)
                case NumOp.>    => rangeQuery(stdField(field)).gt(value)
                case NumOp.>=   => rangeQuery(stdField(field)).gte(value)
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
        } // loop
        loop(query.ct, boolQuery())
      }
    } // Materializer
}

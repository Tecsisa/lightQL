package com.tecsisa.wr
package kql
package mat

import com.tecsisa.wr.kql.ast.ClauseTree.{ Clause, CombinedClause }
import com.tecsisa.wr.kql.ast.LogicOperator.{ and, or }
import com.tecsisa.wr.kql.ast.EqualityOperator.<>
import com.tecsisa.wr.kql.ast.{ ClauseTree, Kql, Search }
import org.elasticsearch.action.search.SearchRequest
import org.elasticsearch.client.Requests
import org.elasticsearch.index.query.{ BoolQueryBuilder, QueryBuilder }
import org.elasticsearch.index.query.QueryBuilders.{ boolQuery, termQuery }
import org.elasticsearch.search.builder.SearchSourceBuilder.searchSource

trait Materializer[+Q] {
  def asQuery(kql: Kql): Q
}

object Materializer {
  implicit def esMaterializer: Materializer[SearchRequest] = new Materializer[SearchRequest] {
    def asQuery(kql: Kql): SearchRequest = kql match {
      case Search(types, indexes, limit, query) =>
        val qb = buildQuery(query.get)
        println(qb.toString)
        Requests
          .searchRequest(indexes.map(_.name): _*)
          .types(types.map(_.name): _*)
          .source(searchSource().query(qb)) // TODO
      case _ => throw new Exception
    }
    // https://www.elastic.co/guide/en/elasticsearch/guide/current/combining-filters.html
    private def buildQuery(query: ClauseTree): QueryBuilder = {
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
            case `<>` => qb.mustNot(loop(ct, qb))
            case _    => f(loop(ct, qb))
          }
        case _: CombinedClause => f(loop(ct, boolQuery()))
      }
      boolQuery().filter(loop(query, boolQuery()))
    }
  }
}

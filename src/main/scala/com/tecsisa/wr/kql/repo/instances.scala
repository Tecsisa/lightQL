package com.tecsisa.wr
package kql
package repo

import org.elasticsearch.action.ActionFuture
import org.elasticsearch.action.search.{ SearchRequest, SearchResponse }
import org.elasticsearch.client.Client
import scala.language.implicitConversions

package object instances extends RepoInstances

trait RepoInstances {
  implicit def esRepo: Repo[Client, SearchRequest] = new ESRepo

  // ops for `SearchRequest`
  implicit def searchRequestOps(underlying: SearchRequest)(
      implicit repo: Repo[Client, SearchRequest]): SearchRequestOps = {
    new SearchRequestOps(underlying, repo)
  }
}

class ESRepo extends Repo[Client, SearchRequest] {
  type R = ActionFuture[SearchResponse]
  def search(conn: Client, query: SearchRequest): ActionFuture[SearchResponse] =
    conn.search(query)
}

class SearchRequestOps(underlying: SearchRequest, val repo: Repo[Client, SearchRequest]) {
  def search()(implicit conn: Client) = repo.search(conn, underlying)
}

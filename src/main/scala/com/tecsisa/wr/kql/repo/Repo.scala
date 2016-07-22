package com.tecsisa.wr
package kql
package repo

import org.elasticsearch.action.search.{ SearchRequest, SearchResponse }
import org.elasticsearch.client.Client
import scala.concurrent.Future

trait Repo[+C, -Q, +R] {
//  type R
  def conn: C
  def search(query: Q): R
}

object Repo {
  def apply(client: Client): Repo[Client, SearchRequest, Future[SearchResponse]] =
    new Repo[Client, SearchRequest, Future[SearchResponse]] {
//      type R = ActionFuture[SearchResponse]
      val conn: Client = client
      def search(query: SearchRequest): Future[SearchResponse] = {
        val listener = new ActionListenerAdapter
        listener.executeFuture(conn, query)
      }
    }
}

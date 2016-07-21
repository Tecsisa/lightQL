package com.tecsisa.wr
package kql
package repo

import org.elasticsearch.action.ActionFuture
import org.elasticsearch.action.search.{ SearchRequest, SearchResponse }
import org.elasticsearch.client.Client

trait Repo[+C, -Q, +R] {
//  type R
  def conn: C
  def search(query: Q): R
}

object Repo {
  def apply(client: Client): Repo[Client, SearchRequest, ActionFuture[SearchResponse]] =
    new Repo[Client, SearchRequest, ActionFuture[SearchResponse]] {
//      type R = ActionFuture[SearchResponse]
      val conn: Client = client
      def search(query: SearchRequest): ActionFuture[SearchResponse] =
        conn.search(query)
    }
}

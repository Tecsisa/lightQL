package com.tecsisa.wr
package kql
package repo

import org.elasticsearch.action.ActionListener
import org.elasticsearch.action.search.{ SearchRequest, SearchResponse }
import org.elasticsearch.client.Client
import scala.concurrent.{ Future, Promise }

class ActionListenerAdapter extends ActionListener[SearchResponse] {
  val promise = Promise[SearchResponse]

  def onResponse(response: SearchResponse) = {
    promise.success(response)
  }

  def onFailure(e: Throwable) = {
    promise.failure(e)
  }

  def executeFuture(conn: Client, request: SearchRequest): Future[SearchResponse] = {
    conn.search(request, this)
    promise.future
  }
}

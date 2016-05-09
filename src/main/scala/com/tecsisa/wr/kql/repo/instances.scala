package com.tecsisa.wr
package kql
package repo

import org.elasticsearch.action.search.{SearchRequestBuilder, SearchResponse}
import org.elasticsearch.client.Client

package object instances extends RepoInstances

trait RepoInstances {
  implicit def esRepo: Repo[Client, SearchRequestBuilder] = new ESRepo
  implicit def dummyRepo: Repo[DummyConn, DummyQuery]     = new DummyRepo
}

class ESRepo extends Repo[Client, SearchRequestBuilder] {
  type R = SearchResponse
  def search(conn: Client, query: SearchRequestBuilder): SearchResponse = ???
}

class DummyRepo extends Repo[DummyConn, DummyQuery] {
  type R = DummyResponse
  def search(conn: DummyConn, query: DummyQuery): DummyResponse = ???
}

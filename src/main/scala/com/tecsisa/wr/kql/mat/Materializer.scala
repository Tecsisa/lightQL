package com.tecsisa.wr
package kql
package mat

import com.tecsisa.wr.kql.ast.Kql
import org.elasticsearch.action.search.SearchRequest

trait Materializer[+Q] {
  def asQuery(kql: Kql): Q
}

object Materializer {
  implicit def esMaterializer = new Materializer[SearchRequest] {
    def asQuery(kql: Kql): SearchRequest = ???
  }
}

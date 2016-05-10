package com.tecsisa.wr
package kql
package mat

import com.tecsisa.wr.kql.ast.Kql
import org.elasticsearch.action.search.SearchRequest

package object instances extends MaterializerInstances

trait MaterializerInstances {
  implicit def esMaterializer: ESMaterializer = new ESMaterializer
}

class ESMaterializer extends Materializer[SearchRequest] {
  def asQuery(kql: Kql): SearchRequest = ???
}

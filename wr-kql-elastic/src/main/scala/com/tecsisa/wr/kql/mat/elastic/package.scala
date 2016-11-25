package com.tecsisa.wr
package kql
package mat

import com.sksamuel.elastic4s.searches.SearchDefinition
import com.tecsisa.wr.kql.ast.Query
import org.elasticsearch.index.query.QueryBuilder

package object elastic {

  implicit class RichSearchDefinition(sd: SearchDefinition) {
    def query(q: Query)(implicit mat: Materializer[QueryBuilder]): SearchDefinition =
      sd.query2(mat.materialize(q))
  }

}

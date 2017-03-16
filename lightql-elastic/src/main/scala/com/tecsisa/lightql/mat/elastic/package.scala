/*
 * Copyright (C) 2016 TECNOLOGIA, SISTEMAS Y APLICACIONES S.L. <http://www.tecsisa.com>
 */
package com.tecsisa.lightql
package mat

import com.sksamuel.elastic4s.searches.SearchDefinition
import com.sksamuel.elastic4s.searches.queries.QueryDefinition
import com.tecsisa.lightql.ast.Query

package object elastic {

  def materialize(query: Query)(implicit mat: Materializer[QueryDefinition]): QueryDefinition =
    mat.materialize(query)

  implicit class RichSearchDefinition(sd: SearchDefinition) {
    def query(q: Query)(implicit mat: Materializer[QueryDefinition]): SearchDefinition =
      sd.query(mat.materialize(q))
  }

}

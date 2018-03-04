/*
 * Copyright (C) 2016 - 2018 TECNOLOGIA, SISTEMAS Y APLICACIONES S.L. <http://www.tecsisa.com>
 */

package com.tecsisa.lightql.mat.elastic

import cats.Show
import com.sksamuel.elastic4s.searches.SearchDefinition
import com.sksamuel.elastic4s.searches.queries.QueryDefinition
import com.tecsisa.lightql.ast.Query
import com.tecsisa.lightql.mat.Materializer

trait ElasticMaterializerHelpers {
  def materialize(query: Query)(implicit mat: Materializer[QueryDefinition]): QueryDefinition =
    mat.materialize(query)

  implicit class RichSearchDefinition(sd: SearchDefinition) {
    def query(q: Query)(implicit mat: Materializer[QueryDefinition]): SearchDefinition =
      sd.query(mat.materialize(q))
    def show(implicit show: Show[SearchDefinition]): String = show.show(sd)
  }
}

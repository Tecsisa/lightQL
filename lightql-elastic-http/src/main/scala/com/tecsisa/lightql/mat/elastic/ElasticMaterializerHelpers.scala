/*
 * Copyright (C) 2016 - 2021 TECNOLOGIA, SISTEMAS Y APLICACIONES S.L. <http://www.tecsisa.com>
 */

package com.tecsisa.lightql.mat.elastic

import com.sksamuel.elastic4s.Show
import com.sksamuel.elastic4s.requests.searches.SearchRequest
import com.sksamuel.elastic4s.requests.searches.queries.{ Query => EsQuery }
import com.tecsisa.lightql.ast.Query
import com.tecsisa.lightql.mat.Materializer

trait ElasticMaterializerHelpers {
  def materialize(query: Query)(implicit mat: Materializer[EsQuery]): EsQuery =
    mat.materialize(query)

  implicit class RichSearchDefinition(sd: SearchRequest) {
    def query(q: Query)(implicit mat: Materializer[EsQuery]): SearchRequest =
      sd.query(mat.materialize(q))
    def show(implicit show: Show[SearchRequest]): String = show.show(sd)
  }
}

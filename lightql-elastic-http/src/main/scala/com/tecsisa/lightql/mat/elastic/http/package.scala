/*
 * Copyright (C) 2016, 2017 TECNOLOGIA, SISTEMAS Y APLICACIONES S.L. <http://www.tecsisa.com>
 */
package com.tecsisa.lightql
package mat
package elastic

import cats.Show
import com.sksamuel.elastic4s.http.HttpClient
import com.sksamuel.elastic4s.searches.SearchDefinition
import com.sksamuel.elastic4s.searches.queries.QueryDefinition
import com.tecsisa.lightql.ast.Query

package object http {

  import ElasticHttpMaterializer.elasticMaterializer

  def materialize(query: Query): QueryDefinition =
    elasticMaterializer.materialize(query)

  implicit class RichHttpSearchDefinition(sd: SearchDefinition) {
    def query(q: Query): SearchDefinition =
      sd.query(elasticMaterializer.materialize(q))
    def show(implicit client: HttpClient, s: Show[SearchDefinition]): String = client.show(sd)
  }

}

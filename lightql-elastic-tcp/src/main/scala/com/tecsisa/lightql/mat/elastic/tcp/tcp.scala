/*
 * Copyright (C) 2016, 2017 TECNOLOGIA, SISTEMAS Y APLICACIONES S.L. <http://www.tecsisa.com>
 */
package com.tecsisa.lightql
package mat
package elastic

import cats.Show
import com.sksamuel.elastic4s.TcpClient
import com.sksamuel.elastic4s.searches.SearchDefinition
import com.sksamuel.elastic4s.searches.queries.QueryDefinition
import com.tecsisa.lightql.ast.Query

package object tcp {

  import ElasticTcpMaterializer.elasticMaterializer

  def materialize(query: Query): QueryDefinition =
    elasticMaterializer.materialize(query)

  implicit class RichTcpSearchDefinition(sd: SearchDefinition) {
    def query(q: Query): SearchDefinition =
      sd.query(elasticMaterializer.materialize(q))
    def show(implicit client: TcpClient, s: Show[SearchDefinition]): String = client.show(sd)
  }

}

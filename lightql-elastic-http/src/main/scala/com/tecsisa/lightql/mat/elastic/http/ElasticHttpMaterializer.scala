/*
 * Copyright (C) 2016, 2017 TECNOLOGIA, SISTEMAS Y APLICACIONES S.L. <http://www.tecsisa.com>
 */
package com.tecsisa.lightql
package mat
package elastic
package http

import com.sksamuel.elastic4s.http.search.SearchImplicits
import com.sksamuel.elastic4s.searches.queries.term.BuildableTermsQuery

private[http] object ElasticHttpMaterializer extends SearchImplicits {
  implicit val elasticMaterializer: ElasticMaterializer =
    new ElasticMaterializer {
      protected implicit val btq: BuildableTermsQuery[AnyRef] = BuildableTermsNoOp[AnyRef]
    }
}

/*
 * Copyright (C) 2016, 2017 TECNOLOGIA, SISTEMAS Y APLICACIONES S.L. <http://www.tecsisa.com>
 */
/*
 * Copyright (C) 2016, 2017 TECNOLOGIA, SISTEMAS Y APLICACIONES S.L. <http://www.tecsisa.com>
 */
package com.tecsisa.lightql
package mat
package elastic
package tcp

import com.sksamuel.elastic4s.searches.queries.BuildableTermsQueryImplicits
import com.sksamuel.elastic4s.searches.queries.term.BuildableTermsQuery

private[tcp] object ElasticTcpMaterializer extends BuildableTermsQueryImplicits {
  implicit val elasticMaterializer: ElasticMaterializer =
    new ElasticMaterializer {
      protected implicit val btq: BuildableTermsQuery[AnyRef] = AnyRefBuildableTermsQuery
    }
}

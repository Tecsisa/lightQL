/*
 * Copyright (C) 2016 - 2018 TECNOLOGIA, SISTEMAS Y APLICACIONES S.L. <http://www.tecsisa.com>
 */

package com.tecsisa.lightql
package mat
package elastic

import com.sksamuel.elastic4s.http.ElasticDsl._
import com.sksamuel.elastic4s.searches.queries.term.BuildableTermsQuery

package object http extends ElasticMaterializerHelpers {

  implicit val elasticMaterializer: ElasticMaterializer =
    new ElasticMaterializer {
      protected implicit val btq: BuildableTermsQuery[AnyRef] = BuildableTermsNoOp[AnyRef]
    }
}

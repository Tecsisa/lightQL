/*
 * Copyright (C) 2016, 2017 TECNOLOGIA, SISTEMAS Y APLICACIONES S.L. <http://www.tecsisa.com>
 */
package com.tecsisa.lightql
package mat
package elastic

import com.sksamuel.elastic4s.http.search.SearchImplicits
import com.sksamuel.elastic4s.searches.queries.term.BuildableTermsQuery

package object http extends ElasticMaterializerHelpers {

  implicit val elasticMaterializer: ElasticMaterializer =
    new ElasticMaterializer with SearchImplicits {
      protected implicit val btq: BuildableTermsQuery[AnyRef] = BuildableTermsNoOp[AnyRef]
    }
}

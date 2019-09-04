/*
 * Copyright (C) 2016 - 2018 TECNOLOGIA, SISTEMAS Y APLICACIONES S.L. <http://www.tecsisa.com>
 */

package com.tecsisa.lightql
package mat
package elastic

package object http extends ElasticMaterializerHelpers {

  implicit val elasticMaterializer: ElasticMaterializer =
    new ElasticMaterializer {}
}

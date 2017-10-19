/*
 * Copyright (C) 2016, 2017 TECNOLOGIA, SISTEMAS Y APLICACIONES S.L. <http://www.tecsisa.com>
 */

package com.tecsisa.lightql
package mat

import com.tecsisa.lightql.ast.Query

trait Materializer[T] {
  def materialize(query: Query): T
}

package com.tecsisa.wr
package kql
package mat

import com.tecsisa.wr.kql.ast.Kql

trait Materializer[+Q] {
  def asQuery(kql: Kql): Q
}

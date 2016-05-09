package com.tecsisa.wr
package kql
package mat

import com.tecsisa.wr.kql.ast.Kql
import com.tecsisa.wr.kql.repo.DummyQuery

package object instances extends MaterializerInstances

trait MaterializerInstances {
  implicit def dummyMaterializer: DummyMaterializer = new DummyMaterializer
}

class DummyMaterializer extends Materializer[Kql] {
  type Q = DummyQuery
  def asQuery(kql: Kql): DummyQuery = ???
}

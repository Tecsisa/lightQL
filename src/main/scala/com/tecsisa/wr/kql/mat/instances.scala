package com.tecsisa.wr
package kql
package mat

import com.tecsisa.wr.kql.ast.Kql

package object instances extends MaterializerInstances

trait MaterializerInstances {
  implicit def dummyMaterializer: DummyMaterializer = new DummyMaterializer
}

class DummyMaterializer extends Materializer[Kql] {
  type R = Unit
  def execute(kql: Kql): Unit = {
    // Nothing to do
    println("executing...")
  }
}

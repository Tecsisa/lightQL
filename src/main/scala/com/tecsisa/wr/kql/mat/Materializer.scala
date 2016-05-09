package com.tecsisa.wr
package kql
package mat

trait Materializer[A] {
  type Q
  def asQuery(kql: A): Q
}

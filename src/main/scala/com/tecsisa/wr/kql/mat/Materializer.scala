package com.tecsisa.wr
package kql
package mat

trait Materializer[A] {
  type R
  def execute(kql: A): R
}

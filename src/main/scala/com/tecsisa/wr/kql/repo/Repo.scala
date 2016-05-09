package com.tecsisa.wr
package kql
package repo

import scala.language.implicitConversions

trait DummyQuery

object DummyQuery {
  implicit def dummyQueryOps(underlying: DummyQuery): DummyQueryOps =
    new DummyQueryOps(underlying)
}

class DummyQueryOps(underlying: DummyQuery) {
  def search(conn: DummyConn)(implicit repo: Repo[DummyConn, DummyQuery]) =
    repo.search(conn, underlying)
}

trait DummyConn
trait DummyResponse

trait Repo[-C, -Q] {
  type R
  def search(conn: C, query: Q): R
}

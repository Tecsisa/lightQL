package com.tecsisa.wr
package kql
package repo

trait Repo[-C, -Q] {
  type R
  def search(conn: C, query: Q): R
}

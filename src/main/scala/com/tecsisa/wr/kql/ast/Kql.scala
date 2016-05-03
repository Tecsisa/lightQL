package com.tecsisa.wr
package kql
package ast

trait Kql
object Kql {
  case class DocumentType(name: String) extends Kql
  case class IndexName(name: String) extends Kql
  case class Search(
    types: Seq[DocumentType],
    indexes: Option[Seq[IndexName]] = None,
    limit: Option[Limit] = None
  ) extends Kql
  case class Limit(size: Int) extends Kql
}

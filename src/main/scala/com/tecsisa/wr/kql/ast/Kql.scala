package com.tecsisa.wr
package kql
package ast

trait Kql
object Kql {
  case class DocumentType(name: String) extends Kql
  case class IndexName(name: String) extends Kql
  case class Limit(size: Int) extends Kql

  // Query part
  case class Field(name: String) extends Kql
  case class Operator(name: String) extends Kql
  case class Value(name: String) extends Kql
  case class Clause(fieldName: Field, op: Operator, value: Value) extends Kql
  // end of Query Part

  case class Search(
    types: Seq[DocumentType],
    indexes: Seq[IndexName],
    limit: Option[Limit] = None,
<<<<<<< HEAD
    query: Seq[Clause]
=======
    query: Clause // será una Seq[Clause] cuando el lenguaje vaya creciendo
>>>>>>> 48fda23a4dbfbe07eeef186155ffb290b8164714
  ) extends Kql
}

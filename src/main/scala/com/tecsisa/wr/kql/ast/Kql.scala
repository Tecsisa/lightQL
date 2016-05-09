package com.tecsisa.wr
package kql
package ast

import com.tecsisa.wr.kql.mat.Materializer
import scala.language.implicitConversions

trait Kql

object Kql {
  case class DocumentType(name: String) extends Kql
  case class IndexName(name: String)    extends Kql
  case class Limit(size: Int)           extends Kql

  // Query part
  case class Field(name: String)                                  extends Kql
  case class Operator(name: String)                               extends Kql
  case class Value(name: String)                                  extends Kql
  case class Clause(fieldName: Field, op: Operator, value: Value) extends Kql
  // end of Query Part

  case class Search(types: Seq[DocumentType],
                    indexes: Seq[IndexName],
                    limit: Option[Limit] = None,
                    query: Seq[Clause])
      extends Kql

  implicit def kqlOps(underlying: Kql): KqlOps = new KqlOps(underlying)
}

class KqlOps(underlying: Kql) {
  def asQuery(implicit materializer: Materializer[Kql]): materializer.Q =
    materializer.asQuery(underlying)
}

package com.tecsisa.wr
package kql
package ast

import com.tecsisa.wr.kql.mat.Materializer
import scala.language.implicitConversions

sealed trait Kql extends Product with Serializable
object Kql {
  implicit def kqlOps(underlying: Kql): KqlOps = new KqlOps(underlying)
}

final case class DocumentType(name: String) extends Kql
final case class IndexName(name: String)    extends Kql
final case class Limit(size: Int)           extends Kql

sealed trait ClauseTree extends Kql
object ClauseTree {
  type Field = String
  case class Clause[V](field: Field, op: BinaryOperator, value: V)               extends ClauseTree
  case class CombinedClause(lct: ClauseTree, op: LogicOperator, rct: ClauseTree) extends ClauseTree
}

final case class Search(types: Seq[DocumentType],
                        indexes: Seq[IndexName],
                        limit: Option[Limit] = None,
                        query: Option[ClauseTree] = None)
    extends Kql

class KqlOps(underlying: Kql) {
  def asQuery[Q](implicit materializer: Materializer[Q]): Q =
    materializer.asQuery(underlying)
}

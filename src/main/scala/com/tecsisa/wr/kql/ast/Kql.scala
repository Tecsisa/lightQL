package com.tecsisa.wr
package kql
package ast

sealed trait Kql        extends Product with Serializable
sealed trait ClauseTree extends Kql
object ClauseTree {
  type Field = String
  case class Clause[V](field: Field, op: BinaryOperator, value: V)               extends ClauseTree
  case class CombinedClause(lct: ClauseTree, op: LogicOperator, rct: ClauseTree) extends ClauseTree
}
final case class Query(ct: ClauseTree) extends Kql

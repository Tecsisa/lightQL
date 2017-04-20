/*
 * Copyright (C) 2016, 2017 TECNOLOGIA, SISTEMAS Y APLICACIONES S.L. <http://www.tecsisa.com>
 */
package com.tecsisa.lightql
package ast

sealed trait Lightql    extends Product with Serializable
sealed trait ClauseTree extends Lightql
object ClauseTree {
  type Field = String
  case class Clause[V](field: Field, op: BinaryOperator, value: V)               extends ClauseTree
  case class CombinedClause(lct: ClauseTree, op: LogicOperator, rct: ClauseTree) extends ClauseTree
}
final case class Query(ct: ClauseTree) extends Lightql

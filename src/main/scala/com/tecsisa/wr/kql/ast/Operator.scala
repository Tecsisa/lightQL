package com.tecsisa.wr
package kql
package ast

sealed trait Operator       extends Product with Serializable
sealed trait UnaryOperator  extends Operator
sealed trait BinaryOperator extends Operator
sealed trait LogicOperator  extends Operator

object EqualityOperator {
  case object `=` extends BinaryOperator
  case object <>  extends BinaryOperator
}

object NumericOperator {
  case object >  extends BinaryOperator
  case object >= extends BinaryOperator
  case object <  extends BinaryOperator
  case object <= extends BinaryOperator
}

object LogicOperator {
  case object and extends LogicOperator with BinaryOperator
  case object or  extends LogicOperator with BinaryOperator
  case object not extends LogicOperator with UnaryOperator
}

package com.tecsisa.wr
package kql
package ast

import com.tecsisa.wr.kql.ast.Operator._
import com.tecsisa.wr.kql.ast.Operator.Associativity._

sealed trait Operator extends Product with Serializable {
  val precedence: Precedence       = 1
  val associativity: Associativity = Left
}
object Operator {
  type Precedence = Int
  sealed trait Associativity extends Product with Serializable
  object Associativity {
    case object Left  extends Associativity
    case object Right extends Associativity
  }
}
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
}

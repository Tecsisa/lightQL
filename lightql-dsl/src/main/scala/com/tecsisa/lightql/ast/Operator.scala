/*
 * Copyright (C) 2016, 2017 TECNOLOGIA, SISTEMAS Y APLICACIONES S.L. <http://www.tecsisa.com>
 */

package com.tecsisa.lightql
package ast

import Operator._
import Operator.Associativity._

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

sealed trait EqualityOperator extends BinaryOperator
object EqualityOperator {
  case object `=` extends EqualityOperator
  case object !=  extends EqualityOperator
}

sealed trait MatchingOperator extends BinaryOperator
object MatchingOperator {
  case object ~  extends MatchingOperator
  case object !~ extends MatchingOperator
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

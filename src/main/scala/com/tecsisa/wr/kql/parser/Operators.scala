package com.tecsisa.wr
package kql
package parser

import com.tecsisa.wr.kql.ast.EqualityOperator.{`=`, <>}
import com.tecsisa.wr.kql.ast.LogicOperator.{and, not, or}
import com.tecsisa.wr.kql.ast.NumericOperator.{>, >=, <, <=}
import fastparse.all._

trait Operators {
  val eqOperator = P("=".! | "<>".!).map {
    case "="  => `=`
    case "<>" => <>
  }
  val numericOperator = P(">".! | ">=".! | "<".! | "<=".!).map {
    case ">"  => >
    case ">=" => >=
    case "<"  => <
    case "<=" => <=
  }
  val logicOperator =
    P(IgnoreCase("and").! | IgnoreCase("or").! | IgnoreCase("not").!).map(_.toLowerCase).map {
      case "and" => and
      case "or"  => or
      case "not" => not
    }
  val clauseOperator = eqOperator | numericOperator
}

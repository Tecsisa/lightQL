package com.tecsisa.lightql
package parser

import fastparse.all._
import com.tecsisa.lightql.ast.EqualityOperator
import com.tecsisa.lightql.ast.MatchingOperator
import com.tecsisa.lightql.ast.LogicOperator.{ and, or }
import com.tecsisa.lightql.ast.NumericOperator.{ <, <=, >, >= }

trait Operators extends BasicParsers {
  val eqOperator = P("=" | "!=").!.map {
    case "="  => EqualityOperator.`=`
    case "!=" => EqualityOperator.!=
  }
  val matchingOperator = P("~" | "!~").!.map {
    case "~"  => MatchingOperator.~
    case "!~" => MatchingOperator.!~
  }
  val numericOperator = P(">=" | "<=" | "<" | ">").!.map {
    case ">=" => >=
    case "<=" => <=
    case "<"  => <
    case ">"  => >
  }
  val logicOperator =
    monospaced(P(IgnoreCase("and") | IgnoreCase("or")).!).map(_.toLowerCase).map {
      case "and" => and
      case "or"  => or
    }
  val clauseOperator = eqOperator | matchingOperator | numericOperator
}

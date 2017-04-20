/*
 * Copyright (C) 2016, 2017 TECNOLOGIA, SISTEMAS Y APLICACIONES S.L. <http://www.tecsisa.com>
 */
package com.tecsisa.lightql
package parser

import fastparse.all._
import com.tecsisa.lightql.ast.EqualityOperator
import com.tecsisa.lightql.ast.MatchingOperator
import com.tecsisa.lightql.ast.LogicOperator.{ and, or }
import com.tecsisa.lightql.ast.NumericOperator.{ <, <=, >, >= }

private[parser] trait Operators extends BasicParsers {
  protected[this] val eqOperator = P("=" | "!=").!.map {
    case "="  => EqualityOperator.`=`
    case "!=" => EqualityOperator.!=
  }
  protected[this] val matchingOperator = P("~" | "!~").!.map {
    case "~"  => MatchingOperator.~
    case "!~" => MatchingOperator.!~
  }
  protected[this] val numericOperator = P(">=" | "<=" | "<" | ">").!.map {
    case ">=" => >=
    case "<=" => <=
    case "<"  => <
    case ">"  => >
  }
  protected[this] val logicOperator =
    monospaced(P(IgnoreCase("and") | IgnoreCase("or")).!).map(_.toLowerCase).map {
      case "and" => and
      case "or"  => or
    }
  protected[this] val clauseOperator = eqOperator | matchingOperator | numericOperator
}

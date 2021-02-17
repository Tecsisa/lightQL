/*
 * Copyright (C) 2016 - 2021 TECNOLOGIA, SISTEMAS Y APLICACIONES S.L. <http://www.tecsisa.com>
 */

package com.tecsisa.lightql
package parser

import fastparse._
import com.tecsisa.lightql.ast.EqualityOperator
import com.tecsisa.lightql.ast.MatchingOperator
import com.tecsisa.lightql.ast.LogicOperator.{ and, or }
import com.tecsisa.lightql.ast.NumericOperator.{ <, <=, >, >= }

private[parser] trait Operators extends BasicParsers {
  protected[this] def eqOperator[_: P] = P("=" | "!=").!.map {
    case "="  => EqualityOperator.`=`
    case "!=" => EqualityOperator.!=
  }
  protected[this] def matchingOperator[_: P] = P("~" | "!~").!.map {
    case "~"  => MatchingOperator.~
    case "!~" => MatchingOperator.!~
  }
  protected[this] def numericOperator[_: P] = P(">=" | "<=" | "<" | ">").!.map {
    case ">=" => >=
    case "<=" => <=
    case "<"  => <
    case ">"  => >
  }
  protected[this] def logicOperator[_: P] =
    monospaced(P(IgnoreCase("and") | IgnoreCase("or")).!).map(_.toLowerCase).map {
      case "and" => and
      case "or"  => or
    }
  protected[this] def clauseOperator[_: P] = eqOperator | matchingOperator | numericOperator
}

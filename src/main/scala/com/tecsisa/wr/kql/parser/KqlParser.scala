package com.tecsisa.wr
package kql
package parser

import fastparse.noApi._
import com.tecsisa.wr.kql.ast.Query
import com.tecsisa.wr.kql.parser.white._

object KqlParser extends KqlParser

trait KqlParser extends BasicParsers with Operators {

  /** ClauseTree section */
  val clauseTree = P(ClauseTreeParse)

  /** The Expression */
  // format: off
  val expr =
    P(space ~ clauseTree ~ End).map { clauseTree =>
      Query(clauseTree)
    }
  // format: on
}

package com.tecsisa.wr
package kql
package parser

import fastparse.noApi._
import com.tecsisa.wr.kql.ast.Query
import com.tecsisa.wr.kql.parser.white._

object KqlParser extends KqlParser {
  def apply(): Parser[Query] = P(space ~ clauseTree ~ End) map Query
}

trait KqlParser extends BasicParsers with Operators {

  /** ClauseTree section */
  val clauseTree = P(ClauseTreeParse)
}

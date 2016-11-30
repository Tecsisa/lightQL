package com.tecsisa.lightql
package parser

import fastparse.noApi._
import com.tecsisa.lightql.ast.Query
import com.tecsisa.lightql.parser.white._

object LightqlParser extends LightqlParser {
  def apply(): Parser[Query] = P(space ~ clauseTree ~ End) map Query
}

trait LightqlParser extends BasicParsers with Operators {

  /** ClauseTree section */
  val clauseTree = P(ClauseTreeParse)
}

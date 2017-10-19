/*
 * Copyright (C) 2016, 2017 TECNOLOGIA, SISTEMAS Y APLICACIONES S.L. <http://www.tecsisa.com>
 */

package com.tecsisa.lightql
package parser

import fastparse.noApi._
import com.tecsisa.lightql.ast.Query
import com.tecsisa.lightql.parser.white._

object LightqlParser extends LightqlParser {
  def parse(s: String): StringParsed[Query] = (P(space ~ clauseTree ~ End) map Query).parse(s)
}

private[parser] trait LightqlParser extends BasicParsers with Operators {

  /** ClauseTree section */
  protected[this] val clauseTree = P(ClauseTreeParse)
}

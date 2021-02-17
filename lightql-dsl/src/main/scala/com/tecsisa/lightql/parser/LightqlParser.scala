/*
 * Copyright (C) 2016 - 2021 TECNOLOGIA, SISTEMAS Y APLICACIONES S.L. <http://www.tecsisa.com>
 */

package com.tecsisa.lightql
package parser

import com.tecsisa.lightql.ast.Query
import fastparse._

object LightqlParser extends LightqlParser {
  import LightQlWhiteSpace._
  private[this] def lightqlParser[_: P] = P(space ~ clauseTree ~ End) map Query

  def parse(s: String): Parsed[Query] =
    fastparse.parse(input = s, parser = lightqlParser(_))
}

private[parser] trait LightqlParser extends BasicParsers with Operators {

  /** ClauseTree section */
  protected[this] def clauseTree[_: P] = P(ClauseTreeParse.clauseTreeParser())
}

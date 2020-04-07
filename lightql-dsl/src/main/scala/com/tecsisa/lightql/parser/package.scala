/*
 * Copyright (C) 2016 - 2018 TECNOLOGIA, SISTEMAS Y APLICACIONES S.L. <http://www.tecsisa.com>
 */

package com.tecsisa.lightql

import com.tecsisa.lightql.ast.Query
import fastparse._

import fastparse.Parsed

package object parser extends Helpers with LightqlParser {

  def parseQuery[_: P](s: String): Parsed[Query] = LightqlParser.parse(s)

  object LightQlWhiteSpace {
    implicit val white = { implicit ctx: ParsingRun[_] => NoTrace(CharsWhile(Whitespace).?) }
  }
}

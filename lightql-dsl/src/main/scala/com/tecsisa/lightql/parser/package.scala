/*
 * Copyright (C) 2016, 2017 TECNOLOGIA, SISTEMAS Y APLICACIONES S.L. <http://www.tecsisa.com>
 */
package com.tecsisa.lightql

import com.tecsisa.lightql.ast.Query
import fastparse.WhitespaceApi
import fastparse.core.{ Mutable, ParseCtx, Parsed, Parser }

package object parser extends Helpers with LightqlParser {

  type StringParsed[+T] = Parsed[T, Char, String]

  def parse(s: String): StringParsed[Query] = LightqlParser.parse(s)

  protected[parser] type StringParser[+T]        = Parser[T, Char, String]
  protected[parser] type StringParseCtx          = ParseCtx[Char, String]
  protected[parser] type StringMutableSuccess[T] = Mutable.Success[T, Char, String]
  protected[parser] type StringMutableFailure    = Mutable.Failure[Char, String]
  protected[parser] type StringMutable[+T]       = Mutable[T, Char, String]

  protected[parser] val white = WhitespaceApi.Wrapper {
    import fastparse.all._
    NoTrace(CharsWhile(Whitespace).?)
  }

}

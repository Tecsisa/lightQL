/*
 * Copyright (C) 2016 TECNOLOGIA, SISTEMAS Y APLICACIONES S.L. <http://www.tecsisa.com>
 */
package com.tecsisa.lightql

import fastparse.WhitespaceApi
import fastparse.core.{ Mutable, ParseCtx, Parsed, Parser }

package object parser extends Helpers {

  type StringParsed[+T] = Parsed[T, Char, String]

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

/*
 * Copyright (C) 2016 TECNOLOGIA, SISTEMAS Y APLICACIONES S.L. <http://www.tecsisa.com>
 */
package com.tecsisa.lightql

import fastparse.WhitespaceApi
import fastparse.core.{ Mutable, ParseCtx, Parser }

package object parser extends Helpers {

  type StringParser[+T]        = Parser[T, Char, String]
  type StringParseCtx          = ParseCtx[Char, String]
  type StringMutableSuccess[T] = Mutable.Success[T, Char, String]
  type StringMutableFailure    = Mutable.Failure[Char, String]
  type StringMutable[+T]       = Mutable[T, Char, String]

  val white = WhitespaceApi.Wrapper {
    import fastparse.all._
    NoTrace(CharsWhile(Whitespace).?)
  }

}

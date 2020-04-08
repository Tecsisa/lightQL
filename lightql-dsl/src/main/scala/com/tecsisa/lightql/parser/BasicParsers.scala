/*
 * Copyright (C) 2016 - 2018 TECNOLOGIA, SISTEMAS Y APLICACIONES S.L. <http://www.tecsisa.com>
 */

package com.tecsisa.lightql
package parser

import fastparse._
import fastparse.NoWhitespace._

private[parser] trait BasicParsers extends Helpers {

  // A parser for optional spaces
  protected[this] def space[_: P] = P(CharsWhile(Whitespace).?)

  // A parser for one space at least
  protected[this] def oneSpaceAtLeast[_: P] = P(" " ~ space)

  // A parser for digits
  protected[this] def digits[_: P] = P(CharsWhile(Digits))

  // A parser for integral numbers
  protected[this] def integral[_: P] = P("0" | CharIn("1-9") ~ digits.?)

  // A parser for integer numbers
  protected[this] def integer[_: P] = P("-".? ~ integral).!.map(_.toInt)

  // A parser for double numbers (IEEE 754 floating point)
  protected[this] def double[_: P] =
    P("-".? ~ integral ~ "." ~ integral.rep(min = 1, max = 16)).!.map(_.toDouble)

  // A parser for dates (yyyy-MM-dd || yyyy-MM-ddTHH:mm:ss)
  protected[this] def dMHms[_: P]          = P(CharIn("0-9").rep(min = 2, max = 2))
  protected[this] def year[_: P]           = P(CharIn("0-9").rep(min = 4, max = 4))
  protected[this] def seconds[_: P]        = P(dMHms ~ ("." ~ CharIn("0-9").rep(max = 3)).?)
  protected[this] def time[_: P]           = P("T" ~ dMHms ~ ":" ~ dMHms ~ ":" ~ seconds)
  protected[this] def tz[_: P]             = P((("-" | "+") ~ dMHms ~ ":" ~ dMHms) | "Z")
  protected[this] def dateTimeFormat[_: P] = P(year ~ "-" ~ dMHms ~ "-" ~ dMHms ~ time ~ tz.?)
  protected[this] def dateTime[_: P]       = dateTimeFormat.!.map(parseDateTime)
  protected[this] def localDate[_: P]      = P(year ~ "-" ~ dMHms ~ "-" ~ dMHms).!.map(parseLocalDate)
  protected[this] def yearMonth[_: P]      = P(year ~ "-" ~ dMHms).!.map(parseYearMonth)

  // A sequence of chars
  protected[this] def charSeq[_: P] = P(CharIn("A-Z", "a-z", "0-9", "_"))

  // A parser for open parens
  protected[this] def openParen[_: P] = P("(" ~ space)

  // A parser for look ahead of open parens
  protected[this] def openParenLah[_: P] = P(&("("))

  // A parser for closed parens
  protected[this] def closeParen[_: P] = P(space ~ ")")

  // A parser for look ahead of closed parens
  protected[this] def closeParenLah[_: P] = P(space ~ &(")"))

  // A parser for open brackets
  protected[this] def openBracket[_: P] = P("[" ~ space)

  // A parser for open brackets
  protected[this] def nesting[_: P] = P("->")

  // A parser for close brackets
  protected[this] def closeBracket[_: P] = P(space ~ "]")

  // A parser for a parentized block
  protected[this] def parenBlock[T](p: => P[T])(implicit ctx: P[_]): P[Vector[T]] =
    openParen ~ p.rep(sep = ",").map(_.toVector) ~ closeParen

  // A parser for a quoted block
  protected[this] def quoted(p: => P[_])(implicit ctx: P[_]): P[String] =
    P("\"" ~ (space ~ p ~ space).! ~ "\"")

  // A parser for a block surrounded for one space at least
  protected[this] def monospaced[T](p: => P[T])(implicit ctx: P[_]): P[T] =
    P(oneSpaceAtLeast ~ p ~ oneSpaceAtLeast)

  // A parser for a list of elements
  protected[this] def list[T](p: => P[T])(implicit ctx: P[_]): P[List[T]] =
    openBracket ~ (space ~ p ~ space).rep(sep = ",").map(_.toList) ~ closeBracket
}

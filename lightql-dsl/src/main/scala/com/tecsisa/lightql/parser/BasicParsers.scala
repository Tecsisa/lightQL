/*
 * Copyright (C) 2016 - 2018 TECNOLOGIA, SISTEMAS Y APLICACIONES S.L. <http://www.tecsisa.com>
 */

package com.tecsisa.lightql
package parser

import fastparse.all._

private[parser] trait BasicParsers extends Helpers {

  // A parser for optional spaces
  protected[this] val space = P(CharsWhile(Whitespace).?)

  // A parser for one space at least
  protected[this] val oneSpaceAtLeast = P(" " ~ space)

  // A parser for digits
  protected[this] val digits = P(CharsWhile(Digits))

  // A parser for integral numbers
  protected[this] val integral = P("0" | CharIn('1' to '9') ~ digits.?)

  // A parser for integer numbers
  protected[this] val integer = P("-".? ~ integral).!.map(_.toInt)

  // A parser for double numbers (IEEE 754 floating point)
  protected[this] val double =
    P("-".? ~ integral ~ "." ~ integral.rep(min = 1, max = 16)).!.map(_.toDouble)

  // A parser for dates (yyyy-MM-dd || yyyy-MM-ddTHH:mm:ss)
  protected[this] val dMHms          = P(CharIn('0' to '9').rep(min = 2, max = 2))
  protected[this] val year           = P(CharIn('0' to '9').rep(min = 4, max = 4))
  protected[this] val seconds        = P(dMHms ~ ("." ~ CharIn('0' to '9').rep(max = 3)).?)
  protected[this] val time           = P("T" ~ dMHms ~ ":" ~ dMHms ~ ":" ~ seconds)
  protected[this] val tz             = P((("-" | "+") ~ dMHms ~ ":" ~ dMHms) | "Z")
  protected[this] val dateTimeFormat = P(year ~ "-" ~ dMHms ~ "-" ~ dMHms ~ time ~ tz.?)
  protected[this] val dateTime       = dateTimeFormat.!.map(parseDateTime)
  protected[this] val localDate      = P(year ~ "-" ~ dMHms ~ "-" ~ dMHms).!.map(parseLocalDate)
  protected[this] val yearMonth      = P(year ~ "-" ~ dMHms).!.map(parseYearMonth)

  // A sequence of chars
  protected[this] val charSeq = P(CharIn('A' to 'Z', 'a' to 'z', '0' to '9', "_-"))

  // A parser for open parens
  protected[this] val openParen = P("(" ~ space)

  // A parser for look ahead of open parens
  protected[this] val openParenLah = P(&("("))

  // A parser for closed parens
  protected[this] val closeParen = P(space ~ ")")

  // A parser for look ahead of closed parens
  protected[this] val closeParenLah = P(space ~ &(")"))

  // A parser for open brackets
  protected[this] val openBracket = P("[" ~ space)

  // A parser for close brackets
  protected[this] val closeBracket = P(space ~ "]")

  // A parser for a parentized block
  protected[this] def parenBlock[T](p: Parser[T]): Parser[Vector[T]] =
    openParen ~ p.rep(sep = ",").map(_.toVector) ~ closeParen

  // A parser for a quoted block
  protected[this] def quoted[T](p: Parser[T]): Parser[String] =
    P("\"" ~ (space ~ p ~ space).! ~ "\"")

  // A parser for a block surrounded for one space at least
  protected[this] def monospaced[T](p: Parser[T]): Parser[T] =
    P(oneSpaceAtLeast ~ p ~ oneSpaceAtLeast)

  // A parser for a list of elements
  protected[this] def list[T](p: Parser[T]): Parser[List[T]] =
    openBracket ~ (space ~ p ~ space).rep(sep = ",").map(_.toList) ~ closeBracket
}

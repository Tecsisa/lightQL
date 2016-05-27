package com.tecsisa.wr
package kql
package parser

import fastparse.all._

trait BasicParsers extends Helpers {
  // A parser for spaces
  val space = P(CharsWhile(Whitespace).?)

  // A parser for digits
  val digits = P(CharsWhile(Digits))

  // A parser for integral numbers
  val integral = P("0" | CharIn('1' to '9') ~ digits.?)

  // A parser for open parens
  val openParen = P(space ~ "(")

  // A parser for closed parens
  val closeParen = P(space ~ ")")

  // A parser for a parentized block
  def parenBlock[T](p: Parser[T]): Parser[Vector[T]] =
    openParen ~ p.rep(sep = ",").map(_.toVector) ~ closeParen

  // A parser for a quoted block
  def quoted[T](p: Parser[T]): Parser[T] =
    P(space ~ "\"" ~ p ~ "\"")
}

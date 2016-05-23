package com.tecsisa.wr
package kql
package parser

import fastparse.all._
import com.tecsisa.wr.kql.ast.Kql._

object KqlParser extends KqlParser

trait KqlParser extends BasicParsers {

  // A function for valid chars in `document types`
  val DtChars = NamedFunction(!"\"\\_,() ".contains(_: Char), "DtChars")

  // A function for valid chars in `indexes`
  val IndexChars = NamedFunction(!"./\\*?\"<>| ,()".contains(_: Char), "IndexChars")

  // A parser for a block of elements that must be
  // parentized if there're more than one
  def block[T](p: Parser[T]): Parser[Vector[T]] =
    p.map(Vector(_)) | parenBlock(p)

  /** Literal parsers */
  val search   = P(IgnoreCase("search"))
  val `with`   = P(IgnoreCase("with"))
  val query    = P(IgnoreCase("query"))
  val limit    = P(IgnoreCase("limit"))
  val in       = P(IgnoreCase("in"))
  val operator = P("=" | "<=" | ">=" | "<>" | ">" | "<")

  /** Document Types section parsers */
  val dtChars = P(CharsWhile(DtChars))
  val dtName  = P(space ~ dtChars.!).map(DocumentType)
  val dtBlock = block(dtName)

  /** Indexes section parsers */
  val forbiddenIndexChars = P(CharsWhile(IndexChars))
  val indexChars          = P(CharIn('a' to 'z', '0' to '9', "_-"))
  val indexName           = P(space ~ forbiddenIndexChars ~ indexChars.rep)
  val indexBlock          = block(indexName.!.map(x => IndexName(x.trim)))

  /** Query section parsers */
  val field = P(indexChars.rep()) // TODO eliminar dependencia con indexChars
  val value = P(integral | quoted(CharPred(_ != '"').rep))
  val queryClause = P(space ~ field.! ~ space ~ operator.! ~ space ~ value.!).map {
    case (f, op, v) => Clause(Field(f), Operator(op), Value(v))
  }
  val queryBlock   = block(queryClause)
  val querySection = space ~ `with` ~ space ~ query ~ space ~ queryBlock

  /** Limit section */
  val limitSection =
    P(space ~ `with` ~ space ~ limit ~ space ~ integral.!).map(_.toInt).map(Limit)

  /** Index section */
  val indexSection = in ~ indexBlock

  /** Search section */
  val searchSection = search ~ dtBlock

  /** The Expression */
  val expr = // format: off
    P(space ~ searchSection ~ space ~
              indexSection          ~
              limitSection.?        ~
              querySection.?        ~ space ~ End).map {
      case (s, i, l, q) => Search(types = s, indexes = i, limit = l, query = q)
    }
    // format: on
}

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

trait Helpers {

  // Wraps a function with a name
  case class NamedFunction[T, V](f: T => V, name: String) extends (T => V) {
    def apply(t: T): V              = f(t)
    override def toString(): String = name
  }

  // A function for valid whitespaces
  val Whitespace = NamedFunction(" \r\n".contains(_: Char), "Whitespace")

  // A function for valid digits
  val Digits = NamedFunction('0' to '9' contains (_: Char), "Digits")
}

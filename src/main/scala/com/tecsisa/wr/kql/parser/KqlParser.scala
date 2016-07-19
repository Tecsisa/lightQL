package com.tecsisa.wr
package kql
package parser

import com.tecsisa.wr.kql.ast.{ DocumentType, IndexName, Limit, Search }
import fastparse.all._

object KqlParser extends KqlParser

trait KqlParser extends BasicParsers with Operators {

  // A function for valid chars in `document types`
  val DtChars = NamedFunction(!"\"\\_,() ".contains(_: Char), "DtChars")

  // A function for valid chars in `indexes`
  val IndexChars = NamedFunction(!"./\\*?\"<>| ,()".contains(_: Char), "IndexChars")

  // A parser for a block of elements that must be
  // parentized if there're more than one
  def block[T](p: Parser[T]): Parser[Vector[T]] =
    p.map(Vector(_)) | parenBlock(p)

  /** Literal parsers */
  val search = P(IgnoreCase("search"))
  val `with` = P(IgnoreCase("with"))
  val query  = P(IgnoreCase("query"))
  val limit  = P(IgnoreCase("limit"))
  val in     = P(IgnoreCase("in"))

  /** Document Types section parsers */
  val dtChars = P(CharsWhile(DtChars))
  val dtName  = P(space ~ !`in` ~ dtChars.!).map(DocumentType)
  val dtBlock = block(dtName)

  /** Indexes section parsers */
  val forbiddenIndexChars = P(CharsWhile(IndexChars))
  val indexChars          = P(CharIn('a' to 'z', '0' to '9', "_-"))
  val indexName           = P(space ~ forbiddenIndexChars ~ indexChars.rep)
  val indexBlock          = block(indexName.!.map(x => IndexName(x.trim)))

  /** ClauseTree section (for query and filter) */
  val clauseTree = P(ClauseTreeParse)

  /** Query section parsers */
  val querySection = space ~ `with` ~ space ~ query ~ space ~ clauseTree

  /** Limit section */
  val limitSection = P(space ~ `with` ~ space ~ limit ~ space ~ integral.!).map(_.toInt).map(Limit)

  /** Index section */
  val indexSection = in ~ indexBlock

  /** Search section */
  val searchSection = search ~ dtBlock

  /** The Expression */
  // format: off
  val expr =
    P(space ~ searchSection ~ space ~
              indexSection          ~
              limitSection.?        ~
              querySection.?        ~ space ~ End).map {
      case (s, i, l, q) => Search(types = s, indexes = i, limit = l, query = q)
    }
  // format: on
}

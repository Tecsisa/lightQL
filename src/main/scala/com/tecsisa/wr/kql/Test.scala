package com.tecsisa.wr
package kql

import com.tecsisa.wr.kql.ast.Kql._
import fastparse.all._
// search (t1, t2)  in (i1, i2, ...)
// with limit x
// with query ()
// with filter ()

object Test {

  object Parser {
    case class NamedFunction[T, V](f: T => V, name: String) extends (T => V) {
      def apply(t: T) = f(t)
      override def toString() = name
    }
    val Whitespace = NamedFunction(" \r\n".contains(_: Char), "Whitespace")
    val Digits = NamedFunction('0' to '9' contains (_: Char), "Digits")
    // ! es un lookahead negativo que se aplica sobre un parser
    // añadimos el espacio como carácter que no puede aparecer
    val DtChars = NamedFunction(!"\"\\_,() ".contains(_: Char), "DtChars")

    val space = P(CharsWhile(Whitespace).?)
    val digits = P(CharsWhile(Digits))
    val integral = P("0" | CharIn('1' to '9') ~ digits.?)
    val openParen = P(space ~ "(")
    val closeParen = P(space ~ ")")
    def parenBlock[T](p: Parser[T]): Parser[Vector[T]] = openParen ~ p.rep(sep = ",").map(_.toVector) ~ closeParen
    def block[T](p: Parser[T]): Parser[Vector[T]] = p.map(Vector(_)) | parenBlock(p)

    val dtChars = P(CharsWhile(DtChars))
    val dtName: Parser[DocumentType] = P(space ~ dtChars.!).map(DocumentType)
    val dtBlock = block(dtName)
    // https://github.com/elastic/elasticsearch/issues/6736

    val notAllowedIndexStartingChars = CharIn("./\\*?\"<>| ,()") // falta el .
    val compoundingIndexChars = P(CharIn('a' to 'z', '0' to '9', "_-"))
    val indexName: Parser[IndexName] = P(space ~ !notAllowedIndexStartingChars ~ compoundingIndexChars.rep.!).map(IndexName)
    val indexesBlock = parenBlock(indexName)
    val search = P(IgnoreCase("search"))
    val `with` = P(IgnoreCase("with"))
    val limit = P(IgnoreCase("limit"))
    val in = P(IgnoreCase("in"))

    val searchSection = (search ~ dtBlock)
    val inSection = (in ~ indexesBlock)
    val limitSection = P(space ~ `with` ~ space ~ limit ~ space ~ integral.!).map(_.toInt).map(Limit)

    val expr = P(space ~ searchSection ~ space ~ inSection.? ~ space ~ limitSection.? ~ space ~ End)
      .map {
        case (searchSection, inSection, limitSection) => Search(searchSection, inSection, limitSection)
      }

  }

  // search (t1, t2)  in (i1, i2, ...)
  // with limit x
  // with query ()
  // with filter ()

  def main(args: Array[String]): Unit = {
    import Parser._

    // Next three use cases must success
    val target1 = "search (type1, type2, type3) with limit 10"
    val target2 = "search type1 in (index3, index5) with limit 100"
    val target3 = "search (type1, type2) in (index1, index2)"
    // This must fail because has a dot in the beginning of a index name
    val target4 = "search (type1, type2) in (.index1, index2)"
    val r1 = expr.parse(target1)
    val r2 = expr.parse(target2)
    val r3 = expr.parse(target3)
    val r4 = expr.parse(target4)
    println(r1)
    println(r2)
    println(r3)
    println(r4)
  }
}

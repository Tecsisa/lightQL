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
    val DtChars = NamedFunction(!"\"\\_,()".contains(_: Char), "DtChars")

    val space = P(CharsWhile(Whitespace).?)
    val digits = P(CharsWhile(Digits))
    val integral = P("0" | CharIn('1' to '9') ~ digits.?)
    val openParen = P(space ~ "(")
    val closeParen = P(space ~ ")")
    def parenBlock[T](p: Parser[T]): Parser[Vector[T]] =
      openParen ~ p.rep(sep = ",").map(_.toVector) ~ closeParen
    def block[T](p: Parser[T]): Parser[Vector[T]] =
      p.map(Vector(_)) | parenBlock(p)

    val dtChars = P(CharsWhile(DtChars))
    val dtName: Parser[DocumentType] = P(space ~ dtChars.!).map(DocumentType)
    val dtBlock = block(dtName)
    val search = P(IgnoreCase("search"))
    val `with` = P(IgnoreCase("with"))
    val limit = P(IgnoreCase("limit"))
    val limitSection =
      P(space ~ `with` ~ space ~ limit ~ space ~ integral.!).map(_.toInt).map(Limit)
    val searchSection = (search ~ dtBlock)
    val expr = P(space ~ searchSection ~ limitSection.? ~ space ~ End)
      .map {
        case (searchSection, limitSection) =>
          Search(searchSection, limitSection)
      }
  }

  def main(args: Array[String]): Unit = {
    import Parser._
    val target1 = "search (type1, type2, type3) with limit 10"
    val target2 = "search type1"
    val r1 = expr.parse(target1)
    val r2 = expr.parse(target2)
    println(r1)
    println(r2)
  }

}

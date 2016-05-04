package com.tecsisa.wr
package kql

import com.tecsisa.wr.kql.ast.Kql._
import fastparse.all._

object Test {

  object Parser {
    case class NamedFunction[T, V](f: T => V, name: String) extends (T => V) {
      def apply(t: T) = f(t)
      override def toString() = name
    }

    /***************** Helpers *****************/
    // Note, quotedP does not capure, you have to call the bang '!' method in
    // the quotedP argument (p) to make p capture input.
    def quotedP[T](p: Parser[T]): Parser[T] = {
      P(space ~ "\"" ~ p ~ "\"")
    }
    /************* End of helpers **************/

    val Whitespace = NamedFunction(" \r\n".contains(_: Char), "Whitespace")
    val Digits = NamedFunction('0' to '9' contains (_: Char), "Digits")
    /*
       ! Es un lookahead negativo que se aplica sobre un parser.
       Añadimos el espacio como carácter que no puede aparecer como condición de
       parada en el caso en que solo se especifica un tipo en la consulta.
    */
    val DtChars = NamedFunction(!"\"\\_,() ".contains(_: Char), "DtChars")
    // ForbiddenIndexChars is negated already
    val ForbiddenIndexChars =
      NamedFunction(!"./\\*?\"<>| ,()".contains(_: Char), "ForbiddenIndexChars")

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

    // The forbidden index chars are based on this webpage:
    // https://github.com/elastic/elasticsearch/issues/6736
    val forbiddenIndexChars = P(CharsWhile(ForbiddenIndexChars))
    val indexChars = P(CharIn('a' to 'z', '0' to '9', "_-"))
    val indexName =
      P(space ~ forbiddenIndexChars ~ indexChars.rep)
    val indexesBlock = block(indexName.!.map(x => IndexName(x.trim)))
    val search = P(IgnoreCase("search"))
    val `with` = P(IgnoreCase("with"))
    val query = P(IgnoreCase("query"))
    val limit = P(IgnoreCase("limit"))
    val in = P(IgnoreCase("in"))
    val fieldP = P(indexChars.rep())
    val operatorP = P("=" | "<=" | ">=" | "<>" | ">" | "<")

    // Here we parse all characters other than double quotes
    val valueP = P(integral | quotedP(CharPred(_ != '"').rep))
    val queryClause = P(space ~ fieldP.! ~ space ~ operatorP.! ~ space ~ valueP.!).map(
      x => Clause(Field(x._1), Operator(x._2), Value(x._3))
    )

    val queryBlock = block(queryClause)

    val searchSection = (search ~ dtBlock)
    val indexSection = (in ~ indexesBlock)
    val querySection = (space ~ `with` ~ space ~ query ~ space ~ queryBlock)
    val limitSection = P(space ~ `with` ~ space ~ limit ~ space
      ~ integral.!).map(_.toInt).map(Limit)

    val expr = P(space ~ searchSection ~ space ~ indexSection ~ limitSection.?
      ~ querySection ~ space ~ End).map {
      case (searchSection, indexSection, limitSection, querySection) =>
        Search(searchSection, indexSection, limitSection, querySection)
    }
  }

  // search (t1, t2)  in (i1, i2, ...)
  // with limit x
  // with query ()
  // with filter ()

  def main(args: Array[String]): Unit = {
    import Parser._

    // Next three use cases must success
    val target1 = "search type1 in (index3,index5) with limit 100 with query (campo1 <> 24534, campo2 <> 1)"
    val target2 = "search (type1, type2) in ( index1,            index2) with query (campo1 > \"sonciclossanos\")"
    val target3 = "search (type1, type2) in ( index1,            index2) with query campo1 < \"sonciclossanos\"   "
    // This must fail because the last parenthesis is missing
    val target4 = "search (type1, type2) in (index1, index2) with query ( campo1 = \"sonciclossanos\""
    // This must fail because the index section is missing
    val target5 = "search (type1, type2, type3) with limit 10"
    // This must fail because has a dot in the beginning of a index name
    val target6 = "search (type1, type2) in (.index1, index2) with query (field = \"randomStr\")"

    val testList = List(target1, target2, target3, target4, target5, target6)
    testList.map(x => println(expr.parse(x)))
  }
}

package com.tecsisa.wr
package kql
package parser

import com.tecsisa.wr.kql.ast.{DocumentType, IndexName, Search}
import com.tecsisa.wr.kql.parser.KqlParser.expr

class KqlParserSpec extends BaseTest {

  "A KqlParser" should {
    "parse a simple expression with only a single document type and index" in {
      val dt     = "type1"
      val index  = "index1"
      val e      = s"search $dt in $index"
      val parsed = expr.parse(e)
      parsed shouldBe a[Success]
      parsed.get.value shouldBe Search(Vector(DocumentType(dt)), Vector(IndexName(index)))
    }
    "not parse and fail when the document type is not set" in {
      val index  = "index1"
      val e      = s"search in $index"
      val parsed = expr.parse(e)
      parsed shouldBe a[Failure]
    }
    "parse a expression with a simple query clause" in {
      val e = "search type1 in index1 with query field1 = 10 and field2 <> 20 or field3 = 30 and field4 = 40"
      val parsed = expr.parse(e)
      parsed shouldBe a[Success]
      println(parsed.get.value)
    }
  }
}

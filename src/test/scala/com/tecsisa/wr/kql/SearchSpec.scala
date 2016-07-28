package com.tecsisa.wr
package kql

import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s.mappings.FieldType.{ IntegerType, StringType }
import com.sksamuel.elastic4s.testkit.{ ElasticMatchers, ElasticSugar }
import com.tecsisa.wr.kql.mat._
import com.tecsisa.wr.kql.parser.KqlParser.expr
import org.scalatest.WordSpec
import org.scalatest.concurrent.Eventually

class SearchSpec extends WordSpec with ElasticSugar with Eventually with ElasticMatchers {

  client.execute {
    create index "students" mappings (
        mapping("student") fields (
            field("firstname") typed StringType index "not_analyzed",
            field("lastname") typed StringType index "not_analyzed",
            field("age") typed IntegerType
        )
    )
  }.await

  client.execute {
    bulk(
        index into "students/student" fields (
            "firstname" -> "Alfred",
            "lastname"  -> "Winston",
            "age"       -> 25
        )
    )
  }.await

  refresh("students")
  blockUntilCount(1, "students")

  "a search query" should {
    "find the document" in {
      val qs = expr.parse("firstname= \"Alfred\" and lastname =\"Winston\"")
      val q  = qs.get.value
      search in "students" query q should haveTotalHits(1)
    }
  }

}

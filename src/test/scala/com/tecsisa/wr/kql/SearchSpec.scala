package com.tecsisa.wr
package kql

import com.sksamuel.elastic4s.ElasticDsl._
import com.tecsisa.wr.kql.mat._

class SearchSpec extends SearchBaseTest {

  "a search query" should {
    "find exact results in queries with just a single clause" in {
      search in "songs" query q("composer = \"Johann Sebastian Bach\"") should haveTotalHits(1)
      search in "songs" query q("genre != \"Classical\"") should haveTotalHits(7)
    }
    "not find exact results when the field is analyzed" in {
      search in "songs" query q("artist = \"Flaming Lips\"") should haveTotalHits(0)
      search in "songs" query q("artist != \"Flaming Lips\"") should haveTotalHits(10)
    }
    "match results in queries with just a single clause" in {
      search in "songs" query q("name ~ \"Paranoid Android\"") should haveTotalHits(1)
      search in "songs" query q("name ~ \"paranoid\"") should haveTotalHits(1)
      search in "songs" query q("artist ~ \"lips\"") should haveTotalHits(1)
      search in "songs" query q("artist !~ \"lips\"") should haveTotalHits(9)
    }
    "not match results in queries with just a single clause" in {
      search in "songs" query q("artist ~ \"John Coltrane\"") should haveTotalHits(0)
    }
    "find exact results in queries with a combined clause" in {
      val q1 = q("composer = \"Johann Sebastian Bach\" and genre = \"Classical\"")
      search in "songs" query q1 should haveTotalHits(1)
      val q2 = q("composer = \"Johann Sebastian Bach\" or genre = \"Jazz\"")
      search in "songs" query q2 should haveTotalHits(3)
      val q3 = q("composer = \"Johann Sebastian Bach\" and genre != \"Jazz\"")
      search in "songs" query q3 should haveTotalHits(1)
      val q4 = q("composer != \"Johann Sebastian Bach\" and genre = \"Jazz\"")
      search in "songs" query q4 should haveTotalHits(2)
      val q5 = q("composer = \"Johann Sebastian Bach\" or composer != \"Patrick Leonard\"")
      search in "songs" query q5 should haveTotalHits(9)
      val q6 = q("composer != \"Johann Sebastian Bach\" and composer != \"Patrick Leonard\"")
      search in "songs" query q6 should haveTotalHits(8)
      val q7 = q("composer != \"Johann Sebastian Bach\" or composer != \"Patrick Leonard\"")
      search in "songs" query q7 should haveTotalHits(10)
    }
    "match results in queries with a combined clause" in {
      val q1 = q("name ~ \"paranoid\" and artist ~ \"radiohead\"")
      search in "songs" query q1 should haveTotalHits(1)
      val q2 = q("name ~ \"paranoid\" or artist ~ \"miles\"")
      search in "songs" query q2 should haveTotalHits(2)
      val q3 = q("name !~ \"paranoid\" and artist ~ \"madonna\"")
      search in "songs" query q3 should haveTotalHits(1)
      val q4 = q("name ~ \"paranoid\" or artist !~ \"radiohead\"")
      search in "songs" query q4 should haveTotalHits(10)
      val q5 = q("name !~ \"paranoid\" and name !~ \"so what\"")
      search in "songs" query q5 should haveTotalHits(8)
      val q6 = q("name !~ \"paranoid\" or name !~ \"so what\"")
      search in "songs" query q6 should haveTotalHits(10)
    }
    "find exact results in unbalanced queries" in {
      val q1 = q("composer = \"Johann Sebastian Bach\" and genre = \"Classical\" and year = 1955")
      search in "songs" query q1 should haveTotalHits(1)
      val q2 =
        q("composer = \"Johann Sebastian Bach\" and (genre = \"Classical\" and year = 1955)")
      search in "songs" query q2 should haveTotalHits(1)
    }
    "find exact results in balanced queries" in {
      val q1 = q {
        "(composer = \"Johann Sebastian Bach\" and genre = \"Classical\") and " +
          "(year = 1955 or year = 1956)"
      }
      search in "songs" query q1 should haveTotalHits(1)
    }
  }

}

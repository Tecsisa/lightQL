package com.tecsisa.wr
package kql
package mat
package elastic

class SearchSpec extends SearchBaseTest {

  "a search query" should {
    "find exact results in queries with just a single clause" in {
      search("songs") query q("composer = \"Johann Sebastian Bach\"") should haveTotalHits(1)
      search("songs") query q("genre != \"Classical\"") should haveTotalHits(7)
      search("songs") query q("date.full = 2016-02-06") should haveTotalHits(1)
      search("songs") query q("stats.rate.stars = 5.0") should haveTotalHits(2)
    }
    "find exact results in queries with multiple values" in {
      search("songs") query q("composer = [\"Johann Sebastian Bach\", \"Radiohead\"]") should haveTotalHits(
        2)
      search("songs") query q("genre != [\"Classical\", \"Jazz\"]") should haveTotalHits(5)
      search("songs") query q("price != [  1.29, 0.99 ]") should haveTotalHits(6)
    }
    "not find exact results when the field is analyzed" in {
      search("songs") query q("artist = \"Flaming Lips\"") should haveTotalHits(0)
      search("songs") query q("artist != \"Flaming Lips\"") should haveTotalHits(10)
    }
    "match results in queries with just a single clause" in {
      search("songs") query q("name ~ \"Paranoid Android\"") should haveTotalHits(1)
      search("songs") query q("name ~ \"paranoid\"") should haveTotalHits(1)
      search("songs") query q("artist ~ \"lips\"") should haveTotalHits(1)
      search("songs") query q("artist !~ \"lips\"") should haveTotalHits(9)
    }
    "not match results in queries with just a single clause" in {
      search("songs") query q("artist ~ \"John Coltrane\"") should haveTotalHits(0)
    }
    "find exact results in queries with a combined clause" in {
      val q1 = q("composer = \"Johann Sebastian Bach\" and genre = \"Classical\"")
      search("songs") query q1 should haveTotalHits(1)
      val q2 = q("composer = \"Johann Sebastian Bach\" or genre = \"Jazz\"")
      search("songs") query q2 should haveTotalHits(3)
      val q3 = q("composer = \"Johann Sebastian Bach\" and genre != \"Jazz\"")
      search("songs") query q3 should haveTotalHits(1)
      val q4 = q("composer != \"Johann Sebastian Bach\" and genre = \"Jazz\"")
      search("songs") query q4 should haveTotalHits(2)
      val q5 = q("composer = \"Johann Sebastian Bach\" or composer != \"Patrick Leonard\"")
      search("songs") query q5 should haveTotalHits(9)
      val q6 = q("composer != \"Johann Sebastian Bach\" and composer != \"Patrick Leonard\"")
      search("songs") query q6 should haveTotalHits(8)
      val q7 = q("composer != \"Johann Sebastian Bach\" or composer != \"Patrick Leonard\"")
      search("songs") query q7 should haveTotalHits(10)
      val q8 = q("stats.rate.stars = 4.5 or stats.rate.stars = 3.5")
      search("songs") query q8 should haveTotalHits(3)
    }
    "match results in queries with a combined clause" in {
      val q1 = q("name ~ \"paranoid\" and artist ~ \"radiohead\"")
      search("songs") query q1 should haveTotalHits(1)
      val q2 = q("name ~ \"paranoid\" or artist ~ \"miles\"")
      search("songs") query q2 should haveTotalHits(2)
      val q3 = q("name !~ \"paranoid\" and artist ~ \"madonna\"")
      search("songs") query q3 should haveTotalHits(1)
      val q4 = q("name ~ \"paranoid\" or artist !~ \"radiohead\"")
      search("songs") query q4 should haveTotalHits(10)
      val q5 = q("name !~ \"paranoid\" and name !~ \"so what\"")
      search("songs") query q5 should haveTotalHits(8)
      val q6 = q("name !~ \"paranoid\" or name !~ \"so what\"")
      search("songs") query q6 should haveTotalHits(10)
    }
    "find exact results in unbalanced queries" in {
      val q1 = q(
        "composer = \"Johann Sebastian Bach\" and genre = \"Classical\" and date.year = 1955 and price = 0.99")
      search("songs") query q1 should haveTotalHits(1)
      val q2 =
        q("composer = \"Johann Sebastian Bach\" and (genre = \"Classical\" and date.year = 1955)")
      search("songs") query q2 should haveTotalHits(1)
    }
    "find exact results in balanced queries" in {
      val q1 = q {
        "(composer = \"Johann Sebastian Bach\" and genre = \"Classical\") and " +
          "(date.year = 1955 or date.year = 1956)"
      }
      search("songs") query q1 should haveTotalHits(1)
    }
    "find range results in queries with just a single clause" in {
      val q1 = q("date.year <= 1955")
      search("songs") query q1 should haveTotalHits(1)
      val q2 = q("date.year >= 1955")
      search("songs") query q2 should haveTotalHits(10)
      val q3 = q("date.year > 1955")
      search("songs") query q3 should haveTotalHits(9)
      val q4 = q("date.year < 1955")
      search("songs") query q4 should haveTotalHits(0)
      val q5 = q("stats.rate.stars > 2.75")
      search("songs") query q5 should haveTotalHits(8)
    }
    "find range results in queries with a combined clause" in {
      val q1 = q("date.year >= 1967 and date.year < 2002")
      search("songs") query q1 should haveTotalHits(5)
      val q2 = q("date.year > 1967 and date.year <= 2002")
      search("songs") query q2 should haveTotalHits(6)
      val q3 = q("price < 2.11 and price > 0.99")
      search("songs") query q3 should haveTotalHits(7)
      val q4 = q("date.year >= 1985 and price > 1.99")
      search("songs") query q4 should haveTotalHits(1)
      val q5 = q("date.full <= 2016-01-01 and date.full >= 1975-10-06")
      search("songs") query q5 should haveTotalHits(5)
      val q6 = q("price > 0.99 and stats.rate.stars < 4.5 and date.year >= 2000")
      search("songs") query q6 should haveTotalHits(3)
    }
  }

}

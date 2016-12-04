/*
 * Copyright (C) 2016 TECNOLOGIA, SISTEMAS Y APLICACIONES S.L. <http://www.tecsisa.com>
 */
package com.tecsisa.lightql
package mat
package elastic

import com.sksamuel.elastic4s.analyzers.KeywordAnalyzer
import com.sksamuel.elastic4s.testkit.{ ElasticMatchers, ElasticSugar }
import com.tecsisa.lightql.ast.Query
import com.tecsisa.lightql.parser.LightqlParser
import org.scalatest.WordSpec
import org.scalatest.concurrent.Eventually

trait SearchBaseTest extends WordSpec with ElasticSugar with Eventually with ElasticMatchers {

  client.execute {
    createIndex("songs") mappings (
      mapping("song") fields (
        textField("name"),
        textField("artist"),
        textField("composer") analyzer KeywordAnalyzer,
        textField("genre") analyzer KeywordAnalyzer,
        nestedField("date") as (
          dateField("full"),
          intField("year")
        ),
        doubleField("price"),
        nestedField("stats") as (
          nestedField("rate") as doubleField("stars")
        )
      )
    )
  }.await

  client.execute {
    bulk(
      indexInto("songs/song") fields (
        "name"     -> "Paranoid Android",
        "artist"   -> "Radiohead",
        "composer" -> "Radiohead",
        "genre"    -> "Pop/Rock",
        "date"     -> Map("full" -> "2016-01-05", "year" -> 1997),
        "price"    -> 1.26,
        "stats"    -> Map("rate" -> Map("stars" -> 4.5))
      ),
      indexInto("songs/song") fields (
        "name"     -> "Sinfonía núm. 1 en Do mayor, Op. 21. I Adagio molto - Allegro con brio",
        "artist"   -> "Simon Rattle // Berliner Philharmoniker",
        "composer" -> "Ludwig van Beethoven",
        "genre"    -> "Classical",
        "date"     -> Map("full" -> "2016-02-06T00:00:00.000Z", "year" -> 2016),
        "year"     -> 2016,
        "price"    -> 2.45,
        "stats"    -> Map("rate" -> Map("stars" -> 3.5))
      ),
      indexInto("songs/song") fields (
        "name"     -> "So What",
        "artist"   -> "Miles Davis",
        "composer" -> "Miles Davis",
        "genre"    -> "Jazz",
        "date"     -> Map("full" -> "1959-08-17T00:00:00.000Z", "year" -> 1959),
        "price"    -> 1.99,
        "stats"    -> Map("rate" -> Map("stars" -> 5.0))
      ),
      indexInto("songs/song") fields (
        "name"     -> "La Isla Bonita",
        "artist"   -> "Madonna",
        "composer" -> "Patrick Leonard",
        "genre"    -> "Pop/Rock",
        "date"     -> Map("full" -> "1987-02-25T00:00:00.000Z", "year" -> 1987),
        "price"    -> 1.29,
        "stats"    -> Map("rate" -> Map("stars" -> 2.75))
      ),
      indexInto("songs/song") fields (
        "name"     -> "Symphony No.8 in E flat - \"Symphony of a Thousand\" Part One: Hymnus",
        "artist"   -> "Georg Solti // Chicago Symphony Orchestra",
        "composer" -> "Gustav Mahler",
        "genre"    -> "Classical",
        "date"     -> Map("full" -> "1967-10-04T00:00:00.000Z", "year" -> 1967),
        "price"    -> 2.11,
        "stats"    -> Map("rate" -> Map("stars" -> 3.25))
      ),
      indexInto("songs/song") fields (
        "name"     -> "Do You Realize??",
        "artist"   -> "Flaming Lips",
        "composer" -> "Wayne Coyne",
        "genre"    -> "Pop/Rock",
        "date"     -> Map("full" -> "2002-08-19T00:00:00.000Z", "year" -> 2002),
        "price"    -> 1.29,
        "stats"    -> Map("rate" -> Map("stars" -> 4.25))
      ),
      indexInto("songs/song") fields (
        "name"     -> "Don't Know Why",
        "artist"   -> "Norah Jones",
        "composer" -> "Jesse Harris",
        "genre"    -> "Jazz",
        "date"     -> Map("full" -> "2002-07-01T00:00:00.000Z", "year" -> 2002),
        "price"    -> 1.19,
        "stats"    -> Map("rate" -> Map("stars" -> 1.5))
      ),
      indexInto("songs/song") fields (
        "name"     -> "Goldberg Variations: Aria",
        "artist"   -> "Glenn Gould",
        "composer" -> "Johann Sebastian Bach",
        "genre"    -> "Classical",
        "date"     -> Map("full" -> "1955-05-22T00:00:00.000Z", "year" -> 1955),
        "price"    -> 0.99,
        "stats"    -> Map("rate" -> Map("stars" -> 3.0))
      ),
      indexInto("songs/song") fields (
        "name"     -> "Money For Nothing",
        "artist"   -> "Dire Straits",
        "composer" -> "Mark Knopfler",
        "genre"    -> "Pop/Rock",
        "date"     -> Map("full" -> "1985-11-02", "year" -> 1985),
        "price"    -> 1.29,
        "stats"    -> Map("rate" -> Map("stars" -> 3.5))
      ),
      indexInto("songs/song") fields (
        "name"     -> "Smell Like Teen Spirit",
        "artist"   -> "Nirvana",
        "composer" -> "Nirvana",
        "genre"    -> "Pop/Rock",
        "date"     -> Map("full" -> "1991-09-10T00:00:00.000Z", "year" -> 1991),
        "price"    -> 1.34,
        "stats"    -> Map("rate" -> Map("stars" -> 5.0))
      )
    )
  }.await

  refresh("songs")
  blockUntilCount(10, "songs")

  protected def q(qs: String): Query = LightqlParser.parse(qs).get.value

}

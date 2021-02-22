# lightQL

[![Build Status](https://travis-ci.org/Tecsisa/lightQL.svg?branch=master)](https://travis-ci.org/Tecsisa/lightQL)

| Artifact | Scala Version | Maven Central |
| :--- | :---: | :---: |
| lightql-dsl | 2.12 | [![Maven Central](https://img.shields.io/maven-central/v/com.tecsisa/lightql-dsl_2.12.svg)](https://maven-badges.herokuapp.com/maven-central/com.tecsisa/lightql-dsl_2.12) |
| lightql-elastic-http | 2.12 | [![Maven Central](https://img.shields.io/maven-central/v/com.tecsisa/lightql-elastic-http_2.12.svg)](https://maven-badges.herokuapp.com/maven-central/com.tecsisa/lightql-elastic-http_2.12) |
| lightql-dsl | 2.13 | [![Maven Central](https://img.shields.io/maven-central/v/com.tecsisa/lightql-dsl_2.13.svg)](https://maven-badges.herokuapp.com/maven-central/com.tecsisa/lightql-dsl_2.13) |
| lightql-elastic-http | 2.13 | [![Maven Central](https://img.shields.io/maven-central/v/com.tecsisa/lightql-elastic-http_2.13.svg)](https://maven-badges.herokuapp.com/maven-central/com.tecsisa/lightql-elastic-http_2.13) |

**lightQL** is a minimal external search DSL that compiles to Scala. Used in conjunction
with [elastic4s](https://github.com/sksamuel/elastic4s),
lightQL can deliver a better developer experience regarding the work with [Elasticsearch](https://www.elastic.co/products/elasticsearch),
especially in simple scenarios where advanced search capabilities are not required.

lightQL has been developed and open sourced by [Tecsisa](http://www.tecsisa.com/). Everyone is welcome to participate
provided that contribution guidelines are followed.

## Quickstart

lightQL is published to [Maven Central](https://search.maven.org/). Currently, the library is cross-built both for Scala 2.12 and 2.13.


In order to work with the HTTP Elasticsearch materializer (the one only available for the time being),
just include the following dependency to your SBT configuration:

```scala
libraryDependencies += "com.tecsisa" %% "lightql-elastic-http" % "7.3.3"
```

This version is tested against Elasticsearch 7 instances. If you are still working with Elasticsearch 6, please use the latest minor version following the scheme `6.x.y`. 

For a quick check, open a REPL and type:

```scala
import com.tecsisa.lightql.parser.LightqlParser._, com.tecsisa.lightql.mat.elastic.http._

val qs = "foo = 100"

materialize(parse(qs).get.value)
```

On the other hand, if you're a happy [elastic4s](https://github.com/sksamuel/elastic4s) user (and sure you are) you'll be able to take advantage
of the seamless integration between these two libraries and declare search expressions easily:

```scala
import com.tecsisa.lightql.parser.LightqlParser._, com.tecsisa.lightql.mat.elastic.http._, com.sksamuel.elastic4s.ElasticDsl._

def q(qs: String) = parse(qs).get.value

search("songs") query q("composer = \"Johann Sebastian Bach\"")
```

## Constructing queries

Generally speaking, you can apply some intuition about how a query should be
based on your knowledge of SQL and other similar languages. The only important difference
is that derived from the fact that, in search engines, it is always possible to search either
for the exact term or, otherwise, perform an approximate search, also known as a *full-text search*. In terms of lightQL,
the first is called a *filtered query*  while the latter is called a *match query*.

### Filtered queries

*Filtered queries* are composed by terms that use the `=` (pronounced *equals*) or
the `!=` (pronounced *not equals*) operator. These kind of queries perform searches
that filter by exact terms.

Please, see below some examples of these queries:

```scala
import com.tecsisa.lightql.parser.LightqlParser._

parse("composer = \"Johann Sebastian Bach\"") // songs composed by `Johann Sebastian Bach`
// val res0: fastparse.Parsed[com.tecsisa.lightql.ast.Query] = Parsed.Success(Query(Clause(composer,=,Johann Sebastian Bach)), 34)

parse("genre != \"Classical\"") // songs that its genre is `Classical`
// val res1: fastparse.Parsed[com.tecsisa.lightql.ast.Query] = Parsed.Success(Query(Clause(genre,!=,Classical)), 20)

parse("stats->rate.stars = 5.0") // songs starred with 5.0
// val res2: fastparse.Parsed[com.tecsisa.lightql.ast.Query] = Parsed.Success(Query(Nested(stats,Clause(rate.stars,=,5.0))), 23)

parse("composer = \"Johann Sebastian Bach\" and price > 0.99") // songs by `Johann Sebastian Bach` that its price is greater than 0.99
// val res3: fastparse.Parsed[com.tecsisa.lightql.ast.Query] = Parsed.Success(Query(CombinedClause(Clause(composer,=,Johann Sebastian Bach),and,Clause(price,>,0.99))), 51)
```

### Match queries

*Match queries* are composed by terms that use the `~` (pronounced *matches*) or
the `!~` (pronounced *not matches*) operator. These kind of queries perform
*full-text* searches that look for aproximate terms.

Please, see below some examples of this kind of queries:

```scala
import com.tecsisa.lightql.parser.LightqlParser._

parse("name ~ \"paranoid\"") // songs that their name matches the word `paranoid` (maybe Radiohead's `Paranoid Android`?)
// val res4: fastparse.Parsed[com.tecsisa.lightql.ast.Query] = Parsed.Success(Query(Clause(name,~,paranoid)), 17) 

parse("artist !~ \"lips\"") // songs that their artist not matches the word `lips` (maybe not `Flaming Lips` songs?)
// val res5: fastparse.Parsed[com.tecsisa.lightql.ast.Query] = Parsed.Success(Query(Clause(artist,!~,lips)), 16)

parse("name ~ \"paranoid\" and artist !~ \"radiohead\"") // any paranoid song that is not from Radiohead? (I doubt it ;-))
// val res6: fastparse.Parsed[com.tecsisa.lightql.ast.Query] = Parsed.Success(Query(CombinedClause(Clause(name,~,paranoid),and,Clause(artist,!~,radiohead))), 43)
```

Take into account that match query terms only make sense on text fields, but
as we'll see in a moment, it's always possible to define a mixed query with
both match and filter terms.

### Mixed queries

Both exact search terms and approximate ones can take part of the same query,
being called in this case *mixed queries*. For example:

```scala
import com.tecsisa.lightql.parser.LightqlParser._

parse("name ~ \"paranoid\" and price <= 0.99") // paranoid songs that their price is less than or equal to 0.99)
// val res7: fastparse.Parsed[com.tecsisa.lightql.ast.Query] = Parsed.Success(Query(CombinedClause(Clause(name,~,paranoid),and,Clause(price,<=,0.99))), 35)
```

### A cautionary tale

It is important to note that, depending on the way the search engine deals with
*full-text* search, the query results might be confusing sometimes. For example, a
*filtered query* on an Elasticsearch analyzed document field, might not return any results
even if the query string is exactly equals to the indexed value. This apparent mistake
is only understood after being aware of the analytical process carried out during the indexing job.

As an example, let's say that we index a document with some field `name`:

```
name -> 'The Dark Side of the Moon'
```

If this `name` field is a document analyzed field, the indexing process will
include an analysis step that, depending on the analyzer, will decompose the
value in its basic and significant tokens, e.g.:

```
dark
side
moon
```

That's the reason why, the exact term `The Dark Side of the Moon` won't work
in this particular case. For those queries that need to response to exact terms
correctly, be sure that the involved fields are not analyzed.

Please, see [this](https://www.elastic.co/guide/en/elasticsearch/reference/current/analysis.html) for details about the analysis process
on Elasticsearch.

## Term types

lightQL is fully compatible with Elasticsearch in regards to the kind of
terms supported. See below some examples of these terms:

```scala
import com.tecsisa.lightql.parser.LightqlParser._

parse("foo = 25") // integer
parse("foo = 2.0000000000000002") // double
parse("foo = \"foobar\"") // string
parse("foo = [25, -2.4, \"foobar\"]") // heterogeneous list
parse("foo = 2001-07-12") // date
parse("foo = 2001-07-12T12:10:30.002+02:00") // date-time with timezone
```

## Supported queries

You'll find below some examples of the kind of supported queries:

```scala
import com.tecsisa.lightql.parser.LightqlParser._

parse("foo = 25") // filtered (a.k.a. exact query)
parse("foo != 25") // not equals
parse("foo ~ 25") // match query
parse("foo !~ 25") // not matches
parse("foo->name = \"foobar\"") // nested
parse("foo.count = 25") // object
parse("foo->bar.name = \"foobar\"") // object inside nested
parse("foo.bar->count = 25") // nested inside object
parse("foo > 25") // range (greater than)
parse("foo < 25") // range (less than)
parse("foo >= 25")
parse("foo <= 25")
parse("foo = 25 and foo = 10") // combined
parse("foo = 25 or foo = 10")
parse("foo = 25 or foo = 10 and bar = 40")
parse("foo = 25 or (foo = 10 and bar = 40)") // as in SQL, the grammar is left recursive
```

## Acknowledgments

The lightQL team is especially grateful to [@lihaoyi](https://github.com/lihaoyi) for his [fastparse](https://github.com/lihaoyi/fastparse)
library whose combinators have been extensively used in the parsing part. Likewise,
it deserves special mention [@sksamuel](https://github.com/sksamuel) and his [elastic4s](https://github.com/sksamuel/elastic4s) library that makes Scala developers' life
easier when it comes to working with Elasticsearch.

## License

lightQL is licensed under the **[Apache License, Version 2.0](https://www.apache.org/licenses/LICENSE-2.0)** (the
"License"); you may not use this software except in compliance with the License.

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

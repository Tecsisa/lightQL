## Quick start

lightQL is published to [Bintray jcenter][bintray-jcenter] and synchronized
to [Maven Central][maven-central] (albeit the latter is not an exact science). The library is
cross-built both for Scala 2.11 and 2.12.


In order to work with either the HTTP or TCP Elasticsearch materializer (the only ones available for the time being),
just include the proper SBT configuration[^1sbt]:

```scala
libraryDependencies += "com.tecsisa" %% "lightql-elastic-http" % "6.2.0"
```
or
```scala
libraryDependencies += "com.tecsisa" %% "lightql-elastic-tcp" % "6.2.0"
```

Then, open a SBT REPL session and try this sequence of commands:

HTTP:

```tut
import com.tecsisa.lightql.parser._, com.tecsisa.lightql.mat.elastic.http._

val qs = "foo = 100"

materialize(parse(qs).get.value)
```

TCP:

```tut:reset
import com.tecsisa.lightql.parser._, com.tecsisa.lightql.mat.elastic.tcp._

val qs = "foo = 100"

materialize(parse(qs).get.value)
```

On the other hand, if you're a happy [elastic4s][elastic4s-github-url] user (and sure you are) you'll be able to take advantage
of the seamless integration between these two libraries and declare search expressions easily:

HTTP:

```tut:reset
import com.tecsisa.lightql.parser._, com.tecsisa.lightql.mat.elastic.http._, com.sksamuel.elastic4s.http.ElasticDsl._

def q(qs: String) = parse(qs).get.value

search("songs") query q("composer = \"Johann Sebastian Bach\"")
```

TCP:

```tut:reset
import com.tecsisa.lightql.parser._, com.tecsisa.lightql.mat.elastic.tcp._, com.sksamuel.elastic4s.ElasticDsl._

def q(qs: String) = parse(qs).get.value

search("songs") query q("composer = \"Johann Sebastian Bach\"")
```

[^1sbt]: Please, use the 2.3.x installments in case you're working with Elasticsearch 2.3.x.  Please, raise a ticket in case you need a 2.4.x compatible release.

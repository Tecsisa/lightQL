---
layout:   docs
title:    "Constructing lightQL queries"
position: 1
---

# Constructing queries

Generally speaking, you can apply some intuition about how a query should be
based on your knowledge of SQL and other similar languages. The only important difference
is that derived from the fact that, in search engines, it is always possible to search either
for the exact term or, otherwise, perform an approximate search, also known as a *full-text search*. In terms of lightQL,
the first is called a *filtered query*  while the latter is called a *match query*.

## Filtered queries

*Filtered queries* are composed by terms that use the `=` (pronounced *equals*) or
the `!=` (pronounced *not equals*) operator. These kind of queries perform searches
that filter by exact terms.

Please, see below some examples of these queries:

```tut
import com.tecsisa.lightql.parser._

parse("composer = \"Johann Sebastian Bach\"") // songs composed by `Johann Sebastian Bach`
parse("genre != \"Classical\"") // songs that its genre is `Classical`
parse("stats->rate.stars = 5.0") // songs starred with 5.0
parse("composer = \"Johann Sebastian Bach\" and price > 0.99") // songs by `Johann Sebastian Bach` that its price is greater than 0.99
```

## Match queries

*Match queries* are composed by terms that use the `~` (pronounced *matches*) or
the `!~` (pronounced *not matches*) operator. These kind of queries perform
*full-text* searches that look for aproximate terms.

Please, see below some examples of this kind of queries:

```tut
import com.tecsisa.lightql.parser._

parse("name ~ \"paranoid\"") // songs that their name matches the word `paranoid` (maybe Radiohead's `Paranoid Android`?)
parse("artist !~ \"lips\"") // songs that their artist not matches the word `lips` (maybe not `Flaming Lips` songs?)
parse("name ~ \"paranoid\" and artist !~ \"radiohead\"") // any paranoid song that is not from Radiohead? (I doubt it ;-))
```

Take into account that match query terms only make sense on text fields, but
as we'll see in a moment, it's always possible to define a mixed query with
both match and filter terms.

## Mixed queries

Both exact search terms and approximate ones can take part of the same query,
being called in this case *mixed queries*. For example:

```tut
import com.tecsisa.lightql.parser._

parse("name ~ \"paranoid\" and price <= 0.99") // paranoid songs that their price is less than or equal to 0.99)
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

Please, see [this][elastic-analysis] for details about the analysis process
on Elasticsearch.

{% include references.md %}
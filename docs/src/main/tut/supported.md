---
layout: docs
title: "Supported queries"
position: 3
---

# Supported queries

You'll find below some examples of the kind of supported queries:

```tut
import com.tecsisa.lightql.parser._

parse("foo = 25") // filtered (a.k.a. exact query)
parse("foo != 25") // not equals
parse("foo ~ 25") // match query
parse("foo !~ 25") // not matches
parse("foo.name = \"foobar\"") // nested
parse("foo > 25") // range (greater than)
parse("foo < 25") // range (less than)
parse("foo >= 25")
parse("foo <= 25")
parse("foo = 25 and foo = 10") // combined
parse("foo = 25 or foo = 10")
parse("foo = 25 or foo = 10 and bar = 40")
parse("foo = 25 or (foo = 10 and bar = 40)") // as in SQL, the grammar is left recursive
```
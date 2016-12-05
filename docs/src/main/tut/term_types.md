---
layout: docs
title: "Term types"
position: 2
---

# Term types

lightQL is fully compatible with Elasticsearch in regards to the kind of
terms supported. See below some examples of these terms:

```tut
import com.tecsisa.lightql.parser._

parse("foo = 25") // integer
parse("foo = 2.0000000000000002") // double
parse("foo = \"foobar\"") // string
parse("foo = [25, -2.4, \"foobar\"]") // heterogeneous list
parse("foo = 2001-07-12") // date
parse("foo = 2001-07-12T12:10:30.002+02:00") // date-time with timezone
```
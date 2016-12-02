---
layout: docs
title: "Types"
position: 1
---

# Types

### Values

Int: `foo = 25`

Double: `foo = 2.0000000000000002`

String: `foo = \"foobar\"`

List: `foo = [25, -2.4, \"foobar\"]`

Date:

    foo = 2001-07-12
    foo = 2001-07-12T12:10:30.002+02:00
    
Nested:

    foo.name = \"foobar\"
    foo.count = 25

### Operators

#### Equality 

    foo = 25
    foo != 25


#### Matching

    foo ~ \"foobar\"
    foo !~ \"foobar\"

#### Numeric

    foo > 25
    foo < 25
    foo >= 25
    foo <= 25

#### Logic

    foo = 25 or foo = 10
    foo = 15 and bar = 100
---
layout: docs
title: Supported queries
---
# Supported queries

## Find exact values

Filters are very fast and are easily cached. 

If we want find exact values then we have to use `=`, for example a composer name:

    composer = "\Johann Sebastian Bach\"
    
In case that we want find all composers who are not called with one name, we have to use `!=`:

    composer != "\Johann Sebastian Bach\"
    
We can find with multiple values:
   
    genre != [\"Classical\", \"Jazz\"]
    
## Match query

The match query is for full-text search.

    name ~ \"Paranoid Android\"
    name !~ \"Paranoid Android\"
    
# Range queries

Matches documents with fields that have terms within a certain range.

    year > 1955
    year < 1955
    year >= 1955
    year <= 1955
    
## Nested queries

    date.year > 2000
    stats.rate.stars = 4.5
    
## Combined clause

We can use combined clauses using logical `(or, and)` operators:
    
    year > 1955 and year < 2000
    composer != \"Johann Sebastian Bach\" and genre = \"Jazz\"
        
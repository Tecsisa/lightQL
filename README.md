---
layout: home
technologies:
 - first: ["Scala", "wr-kql plugin is completely written in Scala"]
 - second: ["Elastic", "wr-kql uses Elasticsearch full-text search engine"]
 - third: ["Jekyll", "Jekyll allows for the transformation of plain text into static websites and blogs"]
---

# wr-kql

**wr-kql** is a library that facilitates create ElasticSearch queries.

# Prerequisites

You will need to add the following dependency in your `build.sbt`:

    // All releases including intermediate ones are published here,
    // final ones are also published to Maven Central.
    resolvers += Resolver.bintrayRepo("tecsisa", "maven-bintray-repo")
    libraryDependencies ++= Vector(
      ibraryDependencies += "com.tecsisa" %% "wr-kql" % "0.1.0",
      ...
    )
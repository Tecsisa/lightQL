---
layout: home
title:  "Home"
section: "home"
---

[![Build Status](https://travis-ci.org/Tecsisa/lightQL.svg?branch=master)](https://travis-ci.org/Tecsisa/lightQL)

| Artifact | Maven Central |
| :--- | :---: |
| lightql-dsl | [![Maven Central](https://img.shields.io/maven-central/v/com.tecsisa/lightql-dsl_2.11.svg)](https://maven-badges.herokuapp.com/maven-central/com.tecsisa/lightql-dsl_2.11) |
| lightql-elastic-http | [![Maven Central](https://img.shields.io/maven-central/v/com.tecsisa/lightql-elastic-http_2.11.svg)](https://maven-badges.herokuapp.com/maven-central/com.tecsisa/lightql-elastic-http_2.11) |
| lightql-elastic-tcp | [![Maven Central](https://img.shields.io/maven-central/v/com.tecsisa/lightql-elastic-tcp_2.11.svg)](https://maven-badges.herokuapp.com/maven-central/com.tecsisa/lightql-elastic-tcp_2.11) |

lightQL a minimal external search DSL that compiles to a Scala AST and can be materialized to other target languages (a.k.a. APIs),
e.g. the [Elasticsearch API][elastic-java-api]. Used in conjunction with [elastic4s][elastic4s-github-url],
lightQL can deliver a better developer experience regarding the work with [Elasticsearch][elastic-homepage],
especially in simple scenarios where advanced searching capabilities are not required.

{% include_relative quickstart.md %}

## Motivation

Being [elastic4s][elastic4s-github-url] an amazing Scala EDSL to work with Elasticsearch, you'll sometimes find yourself
in need of interpreting external queries coming from a web service request or an UI, possibly even written
by end users that might be familiar with popular query languages as SQL or [JQL][jql-reference].

In that case, what Elasticsearch provides is a [JSON based Query DSL][elastic-query-dsl-reference] that is far from being simple and succinct.
Having to support a lot of features in regards to searching and indexing capabilities, it might be understandable
such a complexity but it's clear that, at least for simple use cases, this DSL seems overcomplicated.

lightQL is aimed at tackling this problem by providing a more simple language-agnostic query DSL for Elasticsearch.
As an additional advantage, the language grammar is not strongly tied to Elasticsearch although the materializer is, so that
it is always possible to implement new materializers that work in future Elasticsearch versions or even for other back-ends,
as [Solr][solr-homepage], that share semantic features with Elasticsearch.

## Contributions

See [Contributing](contributing.html) in order to know how to contribute to this project.

{% include_relative acks.md %}

## License

lightQL is licensed under the **[Apache License, Version 2.0][apache-license]** (the
"License"); you may not use this software except in compliance with the License.

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

{% include references.md %}

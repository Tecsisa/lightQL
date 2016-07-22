package com.tecsisa.wr
package kql
package parser

import com.tecsisa.wr.kql.ast.Search
import org.scalatest.{Matchers, WordSpec}

trait BaseTest extends WordSpec with Matchers {
  type Success = fastparse.core.Parsed.Success[Search]
  type Failure = fastparse.core.Parsed.Failure
}

/**

val settings = Settings.settingsBuilder().put("cluster.name", "wr-pre").build()
val client = TransportClient.builder().settings(settings).build().addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("192.168.0.204"), 9300))
val repo = Repo(client)
//val e = "search type1 in index1 with query price = 20 and product = 30 or user <> 10"
val e = "search employee in megacorp with query age = 35"
val parsed = expr.parse(e)
val q = parsed.get.value.asQuery
val res = repo.search(q)
res.map(_.getHits().getTotalHits).foreach(println)
  */
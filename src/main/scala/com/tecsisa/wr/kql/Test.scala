package com.tecsisa.wr
package kql

object Test {

  // search (t1, t2)  in (i1, i2, ...)
  // with limit x
  // with query ()
  // with filter ()

  def main(args: Array[String]): Unit = {
    import parser.KqlParser._
//    import mat.instances._

    // Next three use cases must success
    val target1 =
      "search type1 in (index3,index5) with limit 100 with query (campo1 <> 24534, campo2 <> 1)"
    val target2 =
      "search (type1, type2) in ( index1,            index2) with query (campo1 > \"sonciclossanos\")"
    val target3 =
      "search (type1, type2) in ( index1,            index2) with query campo1 < \"sonciclossanos\"   "
    // This must fail because the last parenthesis is missing
    val target4 =
      "search (type1, type2) in (index1, index2) with query ( campo1 = \"sonciclossanos\""
    // This must fail because the index section is missing
    val target5 = "search (type1, type2, type3) with limit 10"
    // This must fail because has a dot in the beginning of a index name
    val target6 =
      "search (type1, type2) in (.index1, index2) with query (field = \"randomStr\")"

    val testList = List(target1, target2, target3, target4, target5, target6)
    testList.foreach { x =>
      val parsed = expr.parse(x)
      println(parsed)
//      val search = parsed.get.value
//      search.execute()
    }
  }
}

package com.tecsisa.wr
package kql
package parser

import com.tecsisa.wr.kql.ast.Kql.Search
import org.scalatest.{Matchers, WordSpec}

trait BaseTest extends WordSpec with Matchers  {

  type Success = fastparse.core.Parsed.Success[Search]

}

/*
 * Copyright (C) 2016 - 2021 TECNOLOGIA, SISTEMAS Y APLICACIONES S.L. <http://www.tecsisa.com>
 */

package com.tecsisa.lightql
package parser

import com.tecsisa.lightql.ast.Query
import fastparse.Parsed.Success
import org.scalatest.matchers.{ MatchResult, Matcher }
import org.scalatest.matchers.should.Matchers

trait QueryMatchers extends Matchers {

  type Success = fastparse.Parsed.Success[Query]
  type Failure = fastparse.Parsed.Failure

  class ParseToMarcher(query: Query) extends Matcher[String] {
    def apply(left: String): MatchResult = {
      val parsed = LightqlParser.parse(left)
      val (msg, passes) = parsed match {
        case Success(v, _)  => (s"but parsed to $v", parsed.get.value == query)
        case error: Failure => (s"with failure: ${error.msg}", false)
      }
      MatchResult(
        passes,
        s"The string `$left` didn't match the expected query: $query $msg",
        s"The string `$left` matched the expected query: $query"
      )
    }
  }

  def parseTo(query: Query) = new ParseToMarcher(query)

  class NotParseMatcher extends Matcher[String] {
    def apply(left: String): MatchResult = {
      val parsed = LightqlParser.parse(left)
      val (msg, passes) = parsed match {
        case Success(v, _)  => (v, false)
        case error: Failure => (error.msg, true)
      }
      MatchResult(
        passes,
        s"The string `$left` should not parse, however it did parse to: $msg",
        s"The string did not parse wih failure: $msg"
      )
    }
  }

  val notParse = new NotParseMatcher
}

object QueryMatchers extends QueryMatchers

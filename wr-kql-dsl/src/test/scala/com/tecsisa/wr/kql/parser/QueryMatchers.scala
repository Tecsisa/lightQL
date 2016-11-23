package com.tecsisa.wr
package kql
package parser

import com.tecsisa.wr.kql.ast.Query
import fastparse.core.Parsed.Success
import org.scalatest.Matchers
import org.scalatest.matchers.{ MatchResult, Matcher }

trait QueryMatchers extends Matchers {

  type Success = fastparse.core.Parsed.Success[Query, _, _]
  type Failure = fastparse.core.Parsed.Failure[_, _]

  def parseTo(query: Query): Matcher[String] = new Matcher[String] {
    override def apply(left: String): MatchResult = {
      val parsed = KqlParser().parse(left)
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

  def notParse: Matcher[String] = new Matcher[String] {
    override def apply(left: String): MatchResult = {
      val parsed = KqlParser().parse(left)
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

}

object QueryMatchers extends QueryMatchers

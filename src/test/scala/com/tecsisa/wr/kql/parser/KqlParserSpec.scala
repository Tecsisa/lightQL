package com.tecsisa.wr
package kql
package parser

import com.tecsisa.wr.kql.ast.ClauseTree.{ Clause, CombinedClause }
import com.tecsisa.wr.kql.ast.{ EqualityOperator => EqOp }
import com.tecsisa.wr.kql.ast.{ MatchingOperator => MatchOp }
import com.tecsisa.wr.kql.ast.LogicOperator.{ and, or }
import com.tecsisa.wr.kql.ast.Query
import org.scalatest.WordSpec

class KqlParserSpec extends WordSpec with QueryMatchers {

  "A KqlParser" should {
    "parse: `foo = 25`" in {
      "foo = 25" should parseTo { Query(Clause("foo", EqOp.`=`, 25)) }
    }
    "parse: `foo = \"foobar\"`" in {
      "foo = \"foobar\"" should parseTo { Query(Clause("foo", EqOp.`=`, "foobar")) }
    }
    "parse: `foo != 25`" in {
      "foo != 25" should parseTo { Query(Clause("foo", EqOp.!=, 25)) }
    }
    "parse: `foo ~ \"foobar\"`" in {
      "foo ~ \"foobar\"" should parseTo { Query(Clause("foo", MatchOp.~, "foobar")) }
    }
    "parse: `foo !~ \"foobar\"`" in {
      "foo !~ \"foobar\"" should parseTo { Query(Clause("foo", MatchOp.!~, "foobar")) }
    }
    "parse: `foo = 25 and bar = 100`" in {
      "foo = 25 and bar = 100" should parseTo {
        Query(CombinedClause(Clause("foo", EqOp.`=`, 25), and, Clause("bar", EqOp.`=`, 100)))
      }
    }
    "parse: `foo = 25 and bar = 100 or baz = 150`" in {
      "foo = 25 and bar = 100 or baz = 150" should parseTo {
        Query(
          CombinedClause(
            CombinedClause(Clause("foo", EqOp.`=`, 25), and, Clause("bar", EqOp.`=`, 100)),
            or,
            Clause("baz", EqOp.`=`, 150)
          )
        )
      }
    }
    "parse: `(foo = 25 and bar = 100) or baz = 150`" in {
      "(foo = 25 and bar = 100) or baz = 150" should parseTo {
        Query(
          CombinedClause(
            CombinedClause(Clause("foo", EqOp.`=`, 25), and, Clause("bar", EqOp.`=`, 100)),
            or,
            Clause("baz", EqOp.`=`, 150)
          )
        )
      }
    }
    "parse: `foo = 25 and (bar = 100 or baz = 150)`" in {
      "foo = 25 and (bar = 100 or baz = 150)" should parseTo {
        Query(
          CombinedClause(
            Clause("foo", EqOp.`=`, 25),
            and,
            CombinedClause(Clause("bar", EqOp.`=`, 100), or, Clause("baz", EqOp.`=`, 150))
          )
        )
      }
    }
    "not parse: `foo = foobar`" in {
      "foo = foobar" should notParse
    }
    "not parse: `foo != foobar`" in {
      "foo != foobar" should notParse
    }
  }

}

package com.tecsisa.wr
package kql
package parser

import com.tecsisa.wr.kql.ast.ClauseTree.{ Clause, CombinedClause }
import com.tecsisa.wr.kql.ast.{ EqualityOperator => EqOp }
import com.tecsisa.wr.kql.ast.{ MatchingOperator => MatchOp }
import com.tecsisa.wr.kql.ast.{ NumericOperator => NumOp }
import com.tecsisa.wr.kql.ast.LogicOperator.{ and, or }
import com.tecsisa.wr.kql.ast.Query
import org.joda.time.DateTimeZone.UTC
import org.joda.time.DateTime
import org.scalatest.WordSpec

class KqlParserSpec extends WordSpec with QueryMatchers {

  "A KqlParser" should {
    "parse: `foo = 25`" in {
      "foo = 25" should parseTo { Query(Clause("foo", EqOp.`=`, 25)) }
    }
    "parse: `foo <= -25`" in {
      "foo <= -25" should parseTo { Query(Clause("foo", NumOp.<=, -25)) }
    }
    "parse: `foo = 2.0000000000000002`" in {
      "foo = 2.0000000000000002" should parseTo {
        Query(Clause("foo", EqOp.`=`, 2.0000000000000002))
      }
    }
    "parse: `foo = 2001-07-12`" in {
      "foo = 2001-07-12" should parseTo {
        Query(Clause("foo", EqOp.`=`, new DateTime(2001, 7, 12, 0, 0, 0, UTC)))
      }
    }
    "parse: `foo = 2001-07-12T12:10:30.002+02:00`" in {
      "foo = 2001-07-12T12:10:30.002+02:00" should parseTo {
        Query(Clause("foo", EqOp.`=`, new DateTime(2001, 7, 12, 10, 10, 30, 2, UTC)))
      }
    }
    "parse: `foo <= 25`" in {
      "foo <= 25" should parseTo { Query(Clause("foo", NumOp.<=, 25)) }
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
    "parse: `(foo = 25 and (bar = 100))`" in {
      "(foo = 25 and (bar = 100))" should parseTo {
        Query(CombinedClause(Clause("foo", EqOp.`=`, 25), and, Clause("bar", EqOp.`=`, 100)))
      }
    }
    "parse: `foo < 25 and bar >= 100`" in {
      "foo < 25 and bar >= 100" should parseTo {
        Query(CombinedClause(Clause("foo", NumOp.<, 25), and, Clause("bar", NumOp.>=, 100)))
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
    "not parse: `foo = -02.4`" in {
      "foo = -02.4" should notParse
    }
    "not parse: `foo = 1.00000000000000002`" in {
      "foo = 1.00000000000000002" should notParse
    }
    "not parse: `(foo = 25`" in {
      "(foo = 25" should notParse
    }
    "not parse: `foo = 25)`" in {
      "foo = 25)" should notParse
    }
    "not parse: `foo = 25 and ((bar = 100 or baz = 150)`" in {
      "foo = 25 and ((bar = 100 or baz = 150)" should notParse
    }
  }

}

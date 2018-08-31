/*
 * Copyright (C) 2016 - 2018 TECNOLOGIA, SISTEMAS Y APLICACIONES S.L. <http://www.tecsisa.com>
 */

package com.tecsisa.lightql
package parser

import com.tecsisa.lightql.ast.{ EqualityOperator => EqOp }
import com.tecsisa.lightql.ast.{ NumericOperator => NumOp }
import com.tecsisa.lightql.ast.{ MatchingOperator => MatchOp }
import com.tecsisa.lightql.ast.LogicOperator.{ and, or }
import com.tecsisa.lightql.ast.ClauseTree.{ Clause, CombinedClause }
import com.tecsisa.lightql.ast.Query
import org.joda.time.DateTimeZone.UTC
import org.joda.time.{ DateTime, LocalDate, YearMonth }
import org.scalatest.WordSpec

class LightqlParserSpec extends WordSpec with QueryMatchers {

  "Lighthql Parser" should {
    "parse: `foo = 25`" in {
      "foo = 25" should parseTo {
        Query(Clause("foo", EqOp.`=`, 25))
      }
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
        Query(Clause("foo", EqOp.`=`, new LocalDate(2001, 7, 12)))
      }
    }
    "parse: `foo = 2001-07`" in {
      "foo =  2001-07" should parseTo {
        Query(Clause("foo", EqOp.`=`, new YearMonth(2001, 7)))
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
    "parse: `fooBar = \"foobar\"`" in {
      "fooBar = \"foobar\"" should parseTo { Query(Clause("fooBar", EqOp.`=`, "foobar")) }
    }
    "parse: `foo = [25, -2.4, \"foobar\"]`" in {
      "foo = [25, -2.4, \"foobar\"]" should parseTo {
        Query(Clause("foo", EqOp.`=`, List(25, -2.4, "foobar")))
      }
    }
    "parse: `foo->name = \"foobar\"`" in {
      "foo->name = \"foobar\"" should parseTo { Query(Clause("foo->name", EqOp.`=`, "foobar")) }
    }
    "parse: `foo.count = 25`" in {
      "foo.count = 25" should parseTo { Query(Clause("foo.count", EqOp.`=`, 25)) }
    }
    "parse: `foo->bar.name = \"foobar\"`" in {
      "foo->bar.name =  \"foobar\"" should parseTo {
        Query(Clause("foo->bar.name", EqOp.`=`, "foobar"))
      }
    }
    "parse: `foo.bar->count = 25`" in {
      "foo.bar->count = 25" should parseTo { Query(Clause("foo.bar->count", EqOp.`=`, 25)) }
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
    "parse: `foo = 2001-07 and bar = 2001-07-12T12:10:30.002+02:00`" in {
      "foo = 2001-07 and bar = 2001-07-12T12:10:30.002+02:00" should parseTo {
        Query(
          CombinedClause(
            Clause("foo", EqOp.`=`, new YearMonth(2001, 7)),
            and,
            Clause("bar", EqOp.`=`, new DateTime(2001, 7, 12, 10, 10, 30, 2, UTC))))
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

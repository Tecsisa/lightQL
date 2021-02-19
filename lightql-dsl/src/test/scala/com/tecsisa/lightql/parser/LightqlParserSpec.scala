/*
 * Copyright (C) 2016 - 2021 TECNOLOGIA, SISTEMAS Y APLICACIONES S.L. <http://www.tecsisa.com>
 */

package com.tecsisa.lightql
package parser

import com.tecsisa.lightql.ast.{ EqualityOperator => EqOp }
import com.tecsisa.lightql.ast.{ NumericOperator => NumOp }
import com.tecsisa.lightql.ast.{ MatchingOperator => MatchOp }
import com.tecsisa.lightql.ast.LogicOperator.{ and, or }
import com.tecsisa.lightql.ast.ClauseTree.{ Clause, CombinedClause, Nested }
import com.tecsisa.lightql.ast.Query
import org.joda.time.DateTimeZone.UTC
import org.joda.time.{ DateTime, LocalDate, YearMonth }
import org.scalatest.funsuite.AnyFunSuite

class LightqlParserSpec extends AnyFunSuite with QueryMatchers {

  test("foo = 25") {
    "foo = 25" should parseTo {
      Query(Clause("foo", EqOp.`=`, 25))
    }
  }

  test("foo <= -25") {
    "foo <= -25" should parseTo(Query(Clause("foo", NumOp.<=, -25)))
  }

  test("foo = 2.0000000000000002") {
    "foo = 2.0000000000000002" should parseTo {
      Query(Clause("foo", EqOp.`=`, 2.0000000000000002))
    }
  }

  test("foo = 2001-07-12") {
    "foo = 2001-07-12" should parseTo {
      Query(Clause("foo", EqOp.`=`, new LocalDate(2001, 7, 12)))
    }
  }

  test("foo = 2001-07") {
    "foo = 2001-07" should parseTo {
      Query(Clause("foo", EqOp.`=`, new YearMonth(2001, 7)))
    }

  }

  test("foo = 2001-07-12T12:10:30.002+02:00") {
    "foo = 2001-07-12T12:10:30.002+02:00" should parseTo {
      Query(Clause("foo", EqOp.`=`, new DateTime(2001, 7, 12, 10, 10, 30, 2, UTC)))
    }
  }

  test("foo <= 25") {
    "foo <= 25" should parseTo(Query(Clause("foo", NumOp.<=, 25)))
  }

  test("foo = \"foobar\"") {
    "foo = \"foobar\"" should parseTo(Query(Clause("foo", EqOp.`=`, "foobar")))
  }

  test("fooBar = \"foobar\"") {
    "fooBar = \"foobar\"" should parseTo(Query(Clause("fooBar", EqOp.`=`, "foobar")))
  }

  test("foo = [25, -2.4, \"foobar\"]") {
    "foo = [25, -2.4, \"foobar\"]" should parseTo {
      Query(Clause("foo", EqOp.`=`, List(25, -2.4, "foobar")))
    }
  }

  test("foo->name = \"foobar\"") {
    "foo->name = \"foobar\"" should parseTo {
      Query(Nested("foo", Clause("name", EqOp.`=`, "foobar")))
    }
  }

  test("foo.count = 25") {
    "foo.count = 25" should parseTo(Query(Clause("foo.count", EqOp.`=`, 25)))
  }

  test("foo->bar.name = \"foobar\"") {
    "foo->bar.name =  \"foobar\"" should parseTo {
      Query(Nested("foo", Clause("bar.name", EqOp.`=`, "foobar")))
    }
  }

  test("foo.bar->count = 25") {
    "foo.bar->count = 25" should parseTo {
      Query(Nested("foo.bar", Clause("count", EqOp.`=`, 25)))
    }
  }

  test("foo != 25") {
    "foo != 25" should parseTo(Query(Clause("foo", EqOp.!=, 25)))
  }

  test("foo ~ \"foobar\"") {
    "foo ~ \"foobar\"" should parseTo(Query(Clause("foo", MatchOp.~, "foobar")))
  }

  test("foo !~ \"foobar\"") {
    "foo !~ \"foobar\"" should parseTo(Query(Clause("foo", MatchOp.!~, "foobar")))
  }

  test("foo = 25 and bar = 100") {
    "foo = 25 and bar = 100" should parseTo {
      Query(CombinedClause(Clause("foo", EqOp.`=`, 25), and, Clause("bar", EqOp.`=`, 100)))
    }
  }

  test("foo = 2001-07 and bar = 2001-07-12T12:10:30.002+02:00") {
    "foo = 2001-07 and bar = 2001-07-12T12:10:30.002+02:00" should parseTo {
      Query(
        CombinedClause(
          Clause("foo", EqOp.`=`, new YearMonth(2001, 7)),
          and,
          Clause("bar", EqOp.`=`, new DateTime(2001, 7, 12, 10, 10, 30, 2, UTC))
        )
      )
    }
  }

  test("(foo = 25 and (bar = 100))") {
    "(foo = 25 and (bar = 100))" should parseTo {
      Query(CombinedClause(Clause("foo", EqOp.`=`, 25), and, Clause("bar", EqOp.`=`, 100)))
    }
  }

  test("foo < 25 and bar >= 100") {
    "foo < 25 and bar >= 100" should parseTo {
      Query(CombinedClause(Clause("foo", NumOp.<, 25), and, Clause("bar", NumOp.>=, 100)))
    }
  }

  test("foo = 25 and bar = 100 or baz = 150") {
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

  test("(foo = 25 and bar = 100) or baz = 150") {
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

  test("foo = 25 and (bar = 100 or baz = 150)") {
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

  test("foo->bar = 100") {
    "foo->bar = 100" should parseTo {
      Query(
        Nested("foo", Clause("bar", EqOp.`=`, 100))
      )
    }
  }

  test("foo.sub_foo->bar = 100") {
    "foo.sub_foo->bar = 100" should parseTo {
      Query(
        Nested("foo.sub_foo", Clause("bar", EqOp.`=`, 100))
      )
    }
  }

  test("foo.sub_foo->bar.sub_bar = 100") {
    "foo.sub_foo->bar.sub_bar = 100" should parseTo {
      Query(
        Nested("foo.sub_foo", Clause("bar.sub_bar", EqOp.`=`, 100))
      )
    }
  }

  test("foo->bar.elem->elem_foo = 100") {
    "foo->bar.elem->elem_foo = 100" should parseTo {
      Query(
        Nested("foo", Nested("bar.elem", Clause("elem_foo", EqOp.`=`, 100)))
      )
    }
  }

  test("foo->bar.elem->[elem_foo = 100]") {
    "foo->bar.elem->[elem_foo = 100]" should parseTo {
      Query(
        Nested("foo", Nested("bar.elem", Clause("elem_foo", EqOp.`=`, 100)))
      )
    }
  }

  test("foo->[bar = 100 and baz = 150]") {
    "foo->[bar = 100 and baz = 150]" should parseTo {
      Query(
        Nested(
          "foo",
          CombinedClause(Clause("bar", EqOp.`=`, 100), and, Clause("baz", EqOp.`=`, 150))
        )
      )
    }
  }

  test("(foo.elem1 = 4 and foo.elem2 = 5) and foo.nested->[bar = 100 and baz = 150]") {
    "(foo.elem1 = 4 and foo.elem2 = 5) and foo.nested->[bar = 100 and baz = 150]" should parseTo {
      Query(
        CombinedClause(
          CombinedClause(Clause("foo.elem1", EqOp.`=`, 4), and, Clause("foo.elem2", EqOp.`=`, 5)),
          and,
          Nested(
            "foo.nested",
            CombinedClause(Clause("bar", EqOp.`=`, 100), and, Clause("baz", EqOp.`=`, 150))
          )
        )
      )
    }
  }

  test("foo = foobar") {
    "foo = foobar" should notParse
  }

  test("foo != foobar") {
    "foo != foobar" should notParse
  }

  test("foo = -02.4") {
    "foo = -02.4" should notParse
  }

  test("foo = 1.00000000000000002") {
    "foo = 1.00000000000000002" should notParse
  }

  test("(foo = 25") {
    "(foo = 25" should notParse
  }

  test("foo = 25)") {
    "foo = 25)" should notParse
  }

  test("foo = 25 and ((bar = 100 or baz = 150)") {
    "foo = 25 and ((bar = 100 or baz = 150)" should notParse
  }

}

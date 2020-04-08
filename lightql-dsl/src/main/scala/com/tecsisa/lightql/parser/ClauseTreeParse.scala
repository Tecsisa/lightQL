/*
 * Copyright (C) 2016 - 2018 TECNOLOGIA, SISTEMAS Y APLICACIONES S.L. <http://www.tecsisa.com>
 */

package com.tecsisa.lightql
package parser

import fastparse._
import com.tecsisa.lightql.ast.ClauseTree
import com.tecsisa.lightql.ast.ClauseTree.{ Clause, CombinedClause, Nested }
import com.tecsisa.lightql.ast.LogicOperator
import com.tecsisa.lightql.ast.Operator.Associativity
import com.tecsisa.lightql.parser.LightQlWhiteSpace._

import scala.annotation.tailrec

private[parser] case object ClauseTreeParse extends Operators with BasicParsers {

  def element[_: P] =
    P(dateTime | localDate | yearMonth | double | integer | quoted(CharPred(_ != '"').rep))
  def field[_: P] = P(charSeq.rep(sep = ".".?)).!
  def value[_: P] = P(list(element) | element)
  def simpleClause[_: P] = P(field ~ clauseOperator ~ value).map {
    case (f, op, v) => Clause(f, op, v)
  }

  def nestedClause[_: P]: P[Nested[Any]] =
    P(
      (field ~ nesting ~ openBracket ~ clauseTreeParser ~ closeBracket) | (field ~ nesting ~ simpleClause) | (field ~ nesting ~ nestedClause)
    ).map {
      case (f, c) => Nested(f, c)
    }
  def clause[_: P]               = P(simpleClause | nestedClause)
  def logicOperatorSection[_: P] = P(logicOperator)

  def clauseTreeParser()(implicit ctx: P[_]): P[ClauseTree] =
    computeExpr(ctx.index, depth = 0) match { case (ct, _) => ct }

  /*
    Helper methods named after the `precedence climbing` algorithm.
    @see http://eli.thegreenplace.net/2012/08/02/parsing-expressions-by-precedence-climbing
   */

  private def computeAtom(index: Int, depth: Int)(implicit ctx: P[_]): (P[ClauseTree], Int) =
    fastparse.parse(input = ctx.input, parser = openParenLah(_), startIndex = index) match { // lookahead with no consumption
      case _: Parsed.Success[_] =>
        // left paren consumption
        fastparse.parse(input = ctx.input, parser = openParen(_), startIndex = index) match {
          case s: Parsed.Success[_] =>
            // starting on a nested expression
            computeExpr(s.index, depth + 1)
          case f: Parsed.Failure =>
            (ctx.freshFailure(startPos = index), depth)
        }
      case _: Parsed.Failure =>
        // not nested clause
        fastparse.parse(input = ctx.input, parser = clause(_), startIndex = index) match {
          case Parsed.Success(value, sIndex) =>
            (ctx.freshSuccess(value, sIndex, ctx.cut), depth)
          case e: Parsed.Failure =>
            (ctx.freshFailure(startPos = index), depth)
        }
    }

  private def computeExpr(index: Int, depth: Int, minPrec: Int = 1)(
      implicit ctx: P[_]): (P[ClauseTree], Int) =
    computeAtom(index, depth) match {
      case (atom, dep) =>
        fastparse.parse(input = ctx.input, parser = _ => atom, startIndex = index) match {
          case Parsed.Success(atomSuccessValue, atomIndex) =>
            @tailrec
            def loop(lct: ClauseTree, idx: Int, cut: Boolean, dp: Int): (P[ClauseTree], Int) = {
              def current(i: Int = idx, d: Int = dp) =
                (ctx.input.length, i) match {
                  case (`i`, _) if d != 0 => (ctx.freshFailure(startPos = i), d)
                  case _                  => (ctx.freshSuccess(value = lct, index = i, cut = cut), d)
                }

              fastparse.parse(input = ctx.input, parser = logicOperatorSection(_), startIndex = idx) match {
                case Parsed.Success(op, idxOp) =>
                  if (op.precedence < minPrec) {
                    current()
                  } else {
                    val newMinPrec = op.associativity match {
                      case Associativity.Left => op.precedence + 1
                      case _                  => op.precedence
                    }
                    computeExpr(idxOp, dp, newMinPrec) match {
                      case (nextExpr, d) =>
                        fastparse.parse(
                          input = ctx.input,
                          parser = _ => nextExpr,
                          startIndex = index) match {
                          case Parsed.Success(nextExprValue, nextExpIndex) =>
                            loop(computeOp(lct, op, nextExprValue), nextExpIndex, ctx.cut, d)
                          case _: Parsed.Failure =>
                            (ctx.freshFailure(startPos = idxOp), d)
                        }
                    } // nextExpr parsing (recursion)
                  }
                case _: Parsed.Failure =>
                  fastparse.parse(input = ctx.input, parser = closeParenLah(_), startIndex = idx) match { // lookahead with no consumption
                    case _: Parsed.Success[_] =>
                      // right parens consumption (check if this is correct...)
                      fastparse.parse(input = ctx.input, parser = closeParen(_), startIndex = idx) match {
                        case rparen: Parsed.Success[_] =>
                          // success with the expression after consuming right parens
                          val closeParens = rparen.index - idx
                          current(rparen.index, dp - closeParens)
                        case _: Parsed.Failure =>
                          (ctx.freshFailure(startPos = idx), dp)
                      }
                    case _: Parsed.Failure =>
                      // success with the expression with no parens consumption
                      current()
                  }
              } // logicOperator parsing
            } // loop
            loop(atomSuccessValue, atomIndex, atom.cut, dep)
          case _: Parsed.Failure =>
            (ctx.freshFailure(startPos = index), dep)
        }
    } // clause parsing

  private def computeOp(lct: ClauseTree, op: LogicOperator, rct: ClauseTree): ClauseTree =
    CombinedClause(lct, op, rct)
}

/*
 * Copyright (C) 2016 - 2018 TECNOLOGIA, SISTEMAS Y APLICACIONES S.L. <http://www.tecsisa.com>
 */

package com.tecsisa.lightql
package parser

import fastparse.noApi._
import com.tecsisa.lightql.ast.ClauseTree
import com.tecsisa.lightql.ast.ClauseTree.{ Clause, CombinedClause }
import com.tecsisa.lightql.ast.LogicOperator
import com.tecsisa.lightql.ast.Operator.Associativity
import com.tecsisa.lightql.parser.white._

import scala.annotation.tailrec

private[parser] case object ClauseTreeParse
    extends StringParser[ClauseTree]
    with Operators
    with BasicParsers {

  val element =
    P(dateTime | localDate | yearMonth | double | integer | quoted(CharPred(_ != '"').rep))
  val field = P(charSeq.rep(sep = ("." | "->").?)).!
  val value = P(list(element) | element)
  val clause = P(field ~ clauseOperator ~ value).map {
    case (f, op, v) => Clause(f, op, v)
  }
  val logicOperatorSection = P(logicOperator)

  def parseRec(cfg: StringParseCtx, index: Int): StringMutable[ClauseTree] =
    computeExpr(cfg, index, depth = 0) match { case (ct, _) => ct }

  /*
    Helper methods named after the `precedence climbing` algorithm.
    @see http://eli.thegreenplace.net/2012/08/02/parsing-expressions-by-precedence-climbing
   */

  private def computeAtom(
      cfg: StringParseCtx,
      index: Int,
      depth: Int): (StringMutable[ClauseTree], Int) =
    openParenLah.parseRec(cfg, index) match { // lookahead with no consumption
      case _: StringMutableSuccess[_] =>
        // left paren consumption
        openParen.parseRec(cfg, index) match {
          case s: StringMutableSuccess[_] =>
            // starting on a nested expression
            computeExpr(cfg, s.index, depth + 1)
          case f: StringMutableFailure =>
            (failMore(f, index, cfg.logDepth, f.traceParsers, f.cut), depth)
        }
      case _: StringMutableFailure =>
        // not nested clause
        (clause.parseRec(cfg, index), depth)
    }

  private def computeExpr(
      cfg: StringParseCtx,
      index: Int,
      depth: Int,
      minPrec: Int = 1): (StringMutable[ClauseTree], Int) =
    computeAtom(cfg, index, depth) match {
      case (atom: StringMutableSuccess[ClauseTree], dep) =>
        @tailrec
        def loop(
            lct: ClauseTree,
            idx: Int,
            tps: Set[StringParser[_]],
            cut: Boolean,
            dp: Int): (StringMutable[ClauseTree], Int) = {
          def current(i: Int = idx, d: Int = dp) =
            (cfg.input.length, i) match {
              case (`i`, _) if d != 0 => (fail(cfg.failure, i, tps, cut), d)
              case _                  => (success(cfg.success, lct, i, tps, cut), d)
            }
          logicOperatorSection.parseRec(cfg, idx) match {
            case Mutable.Success(op, idxOp, _, _) =>
              if (op.precedence < minPrec) {
                current()
              } else {
                val newMinPrec = op.associativity match {
                  case Associativity.Left => op.precedence + 1
                  case _                  => op.precedence
                }
                computeExpr(cfg, idxOp, dp, newMinPrec) match {
                  case (nextExpr, d) =>
                    nextExpr match {
                      case rct: StringMutableSuccess[ClauseTree] =>
                        loop(computeOp(lct, op, rct.value), rct.index, rct.traceParsers, rct.cut, d)
                      case f: StringMutableFailure =>
                        (failMore(f, idxOp, cfg.logDepth), d)
                    }
                } // nextExpr parsing (recursion)
              }
            case _: StringMutableFailure =>
              closeParenLah.parseRec(cfg, idx) match { // lookahead with no consumption
                case _: StringMutableSuccess[_] =>
                  // right parens consumption (check if this is correct...)
                  closeParen.rep.parseRec(cfg, idx) match {
                    case rparen: StringMutableSuccess[_] =>
                      // success with the expression after consuming right parens
                      val closeParens = rparen.index - idx
                      current(rparen.index, dp - closeParens)
                    case f2: StringMutableFailure =>
                      (failMore(f2, idx, cfg.logDepth, f2.traceParsers, f2.cut), dp)
                  }
                case _: StringMutableFailure =>
                  // success with the expression with no parens consumption
                  current()
              }
          } // logicOperator parsing
        } // loop
        loop(atom.value, atom.index, atom.traceParsers, atom.cut, dep)
      case (f: StringMutableFailure, dep) =>
        (failMore(f, index, cfg.logDepth), dep)
    } // clause parsing

  private def computeOp(lct: ClauseTree, op: LogicOperator, rct: ClauseTree): ClauseTree =
    CombinedClause(lct, op, rct)
}

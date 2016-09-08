package com.tecsisa.wr
package kql
package parser

import fastparse.noApi._
import fastparse.core.{ Mutable, ParseCtx, Parser }
import com.tecsisa.wr.kql.ast.ClauseTree
import com.tecsisa.wr.kql.ast.ClauseTree.{ Clause, CombinedClause }
import com.tecsisa.wr.kql.ast.LogicOperator
import com.tecsisa.wr.kql.ast.Operator.Associativity
import com.tecsisa.wr.kql.parser.white._
import scala.annotation.tailrec

case object ClauseTreeParse extends Parser[ClauseTree] with Operators with BasicParsers {

  val field = P(charSeq.rep).!
  val value = P(date | double | integer | quoted(CharPred(_ != '"').rep))
  val clause = P(field ~ clauseOperator ~ value).map {
    case (f, op, v) => Clause(f, op, v)
  }
  val logicOperatorSection = P(logicOperator)

  def parseRec(cfg: ParseCtx, index: Int): Mutable[ClauseTree] = {
    computeExpr(cfg, index, depth = 0) match { case (ct, _) => ct }
  }

  /*
    Helper methods named after the `precedence climbing` algorithm.
    @see http://eli.thegreenplace.net/2012/08/02/parsing-expressions-by-precedence-climbing
   */

  private def computeAtom(cfg: ParseCtx, index: Int, depth: Int): (Mutable[ClauseTree], Int) = {
    openParenLah.parseRec(cfg, index) match { // lookahead with no consumption
      case _: Mutable.Success[_] =>
        // left paren consumption
        openParen.parseRec(cfg, index) match {
          case s: Mutable.Success[_] =>
            // starting on a nested expression
            computeExpr(cfg, s.index, depth + 1)
          case f: Mutable.Failure =>
            (failMore(f, index, cfg.logDepth, f.traceParsers, f.cut), depth)
        }
      case _: Mutable.Failure =>
        // not nested clause
        (clause.parseRec(cfg, index), depth)
    }
  }

  private def computeExpr(cfg: ParseCtx,
                          index: Int,
                          depth: Int,
                          minPrec: Int = 1): (Mutable[ClauseTree], Int) = {
    computeAtom(cfg, index, depth) match {
      case (atom: Mutable.Success[ClauseTree], depth) =>
        @tailrec
        def loop(lct: ClauseTree,
                 idx: Int,
                 tps: Set[Parser[_]],
                 cut: Boolean,
                 dp: Int): (Mutable[ClauseTree], Int) = {
          def current(i: Int = idx, d: Int = dp) = {
            (cfg.input.length, i) match {
              case (`i`, _) if d != 0 => (fail(cfg.failure, i, tps, cut), d)
              case _                  => (success(cfg.success, lct, i, tps, cut), d)
            }
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
                      case rct: Mutable.Success[ClauseTree] =>
                        loop(computeOp(lct, op, rct.value),
                             rct.index,
                             rct.traceParsers,
                             rct.cut,
                             d)
                      case f: Mutable.Failure =>
                        (failMore(f, idxOp, cfg.logDepth), d)
                    }
                } // nextExpr parsing (recursion)
              }
            case _: Mutable.Failure =>
              closeParenLah.parseRec(cfg, idx) match { // lookahead with no consumption
                case _: Mutable.Success[_] =>
                  // right parens consumption (check if this is correct...)
                  closeParen.rep.parseRec(cfg, idx) match {
                    case rparen: Mutable.Success[_] =>
                      // success with the expression after consuming right parens
                      val closeParens = rparen.index - idx
                      current(rparen.index, dp - closeParens)
                    case f2: Mutable.Failure =>
                      (failMore(f2, idx, cfg.logDepth, f2.traceParsers, f2.cut), dp)
                  }
                case _: Mutable.Failure =>
                  // success with the expression with no parens consumption
                  current()
              }
          } // logicOperator parsing
        }   // loop
        loop(atom.value, atom.index, atom.traceParsers, atom.cut, depth)
      case (f: Mutable.Failure, depth) =>
        (failMore(f, index, cfg.logDepth), depth)
    } // clause parsing
  }

  private def computeOp(lct: ClauseTree, op: LogicOperator, rct: ClauseTree): ClauseTree =
    CombinedClause(lct, op, rct)
}

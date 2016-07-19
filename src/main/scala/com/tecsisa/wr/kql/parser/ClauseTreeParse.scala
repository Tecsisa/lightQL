package com.tecsisa.wr
package kql
package parser

import com.tecsisa.wr.kql.ast.ClauseTree
import com.tecsisa.wr.kql.ast.ClauseTree.{ Clause, CombinedClause }
import com.tecsisa.wr.kql.ast.LogicOperator
import com.tecsisa.wr.kql.ast.Operator.Associativity
import fastparse.all._
import fastparse.core.{ Mutable, ParseCtx, Parser }
import scala.annotation.tailrec

case object ClauseTreeParse extends Parser[ClauseTree] with Operators with BasicParsers {

  val field = P(charSeq.rep)
  val value = P(integral | quoted(CharPred(_ != '"').rep))
  val clause = P(space ~ field.! ~ space ~ clauseOperator ~ space ~ value.!).map {
    case (f, op, v) => Clause(f, op, v)
  }
  val logicOperatorSection = P(space ~ logicOperator)

  def parseRec(cfg: ParseCtx, index: Int): Mutable[ClauseTree] = {
    computeExpr(cfg, index)
  }

  /*
    Helper methods named following the `precedence climbing` algorithm.
    @see http://eli.thegreenplace.net/2012/08/02/parsing-expressions-by-precedence-climbing
   */

  private def computeAtom(cfg: ParseCtx, index: Int): Mutable[ClauseTree] = {
    openParenLah.parseRec(cfg, index) match { // lookahead with no consumption
      case _: Mutable.Success[_] =>
        // left paren consumption
        openParen.parseRec(cfg, index) match {
          case s: Mutable.Success[_] =>
            // starting on a nested expression
            computeExpr(cfg, s.index)
          case f: Mutable.Failure =>
            failMore(f, index, cfg.logDepth, f.traceParsers, f.cut)
        }
      case _: Mutable.Failure =>
        // not nested clause
        clause.parseRec(cfg, index)
    }
  }

  private def computeExpr(cfg: ParseCtx, index: Int, minPrec: Int = 1): Mutable[ClauseTree] = {
    computeAtom(cfg, index) match {
      case atom: Mutable.Success[ClauseTree] =>
        @tailrec
        def loop(lct: ClauseTree,
                 idx: Int,
                 tps: Set[Parser[_]],
                 cut: Boolean): Mutable[ClauseTree] = {
          def current(i: Int = idx) = success(cfg.success, lct, i, tps, cut)
          logicOperatorSection.parseRec(cfg, idx) match {
            case Mutable.Success(op, idxOp, _, _) =>
              if (op.precedence < minPrec) {
                current()
              } else {
                val newMinPrec = op.associativity match {
                  case Associativity.Left => op.precedence + 1
                  case _                  => op.precedence
                }
                val nextExpr = computeExpr(cfg, idxOp, newMinPrec)
                nextExpr match {
                  case rct: Mutable.Success[ClauseTree] =>
                    loop(computeOp(lct, op, rct.value), rct.index, rct.traceParsers, rct.cut)
                  case f: Mutable.Failure =>
                    failMore(f, idxOp, cfg.logDepth)
                } // nextExpr parsing (recursion)
              }
            case _: Mutable.Failure =>
              closeParenLah.parseRec(cfg, idx) match { // lookahead with no consumption
                case _: Mutable.Success[_] =>
                  // right parens consumption (check if this is correct...)
                  closeParen.rep.parseRec(cfg, idx) match {
                    case rparen: Mutable.Success[_] =>
                      // success with the expression after consuming right parens
                      current(rparen.index)
                    case f2: Mutable.Failure =>
                      failMore(f2, idx, cfg.logDepth, f2.traceParsers, f2.cut)
                  }
                case _: Mutable.Failure =>
                  // success with the expression with no parens consumption
                  current()
              }
          } // logicOperator parsing
        }   // loop
        loop(atom.value, atom.index, atom.traceParsers, atom.cut)
      case f: Mutable.Failure =>
        failMore(f, index, cfg.logDepth)
    } // clause parsing
  }

  private def computeOp(lct: ClauseTree, op: LogicOperator, rct: ClauseTree): ClauseTree =
    CombinedClause(lct, op, rct)
}

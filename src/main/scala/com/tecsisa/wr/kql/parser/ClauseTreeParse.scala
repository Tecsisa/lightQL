package com.tecsisa.wr
package kql
package parser

import com.tecsisa.wr.kql.ast.ClauseTree
import com.tecsisa.wr.kql.ast.ClauseTree.{Clause, CombinedClause}
import com.tecsisa.wr.kql.ast.LogicOperator
import fastparse.all._
import fastparse.core.{Mutable, ParseCtx, Parser}
import scala.annotation.tailrec

case object ClauseTreeParse extends Parser[ClauseTree] with Operators with BasicParsers {

  val charSeq = P(CharIn('a' to 'z', '0' to '9', "_-"))
  val field   = P(charSeq.rep)
  val value   = P(integral | quoted(CharPred(_ != '"').rep))
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
    clause.parseRec(cfg, index)
  }

  private def computeExpr(cfg: ParseCtx, index: Int, minPrec: Int = 1): Mutable[ClauseTree] = {
    computeAtom(cfg, index) match {
      case atom: Mutable.Success[ClauseTree] =>
        @tailrec
        def loop(lct: ClauseTree,
                 idx: Int,
                 tps: Set[Parser[_]],
                 cut: Boolean): Mutable[ClauseTree] = {
          def current() = success(cfg.success, lct, idx, tps, cut)
          logicOperatorSection.parseRec(cfg, idx) match {
            case Mutable.Success(op, idxOp, _, _) =>
              if (minPrec > 1) { // TODO: Review precedences
                current()
              } else {
                val nextExpr = computeExpr(cfg, idxOp, minPrec + 1)
                nextExpr match {
                  case rct: Mutable.Success[ClauseTree] =>
                    loop(computeOp(lct, op, rct.value), rct.index, rct.traceParsers, rct.cut)
                  case f: Mutable.Failure =>
                    failMore(f, idxOp, cfg.logDepth)
                } // nextExpr parsing (recursion)
              }
            case f: Mutable.Failure => current()
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

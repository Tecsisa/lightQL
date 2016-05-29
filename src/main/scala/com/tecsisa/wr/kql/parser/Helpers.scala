package com.tecsisa.wr
package kql
package parser

trait Helpers {
  // Wraps a function with a name
  case class NamedFunction[T, V](f: T => V, name: String) extends (T => V) {
    def apply(t: T): V              = f(t)
    override def toString(): String = name
  }

  // A function for valid whitespaces
  val Whitespace = NamedFunction(" \r\n".contains(_: Char), "Whitespace")

  // A function for valid digits
  val Digits = NamedFunction('0' to '9' contains (_: Char), "Digits")
}

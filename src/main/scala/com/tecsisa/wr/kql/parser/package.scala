package com.tecsisa.wr
package kql

import fastparse.WhitespaceApi

package object parser extends Helpers {

  val white = WhitespaceApi.Wrapper {
    import fastparse.all._
    NoTrace(CharsWhile(Whitespace).?)
  }

}

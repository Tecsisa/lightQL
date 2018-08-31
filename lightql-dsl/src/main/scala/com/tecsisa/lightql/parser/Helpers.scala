/*
 * Copyright (C) 2016 - 2018 TECNOLOGIA, SISTEMAS Y APLICACIONES S.L. <http://www.tecsisa.com>
 */

package com.tecsisa.lightql
package parser

import com.github.nscala_time.time.Imports.DateTime
import org.joda.time.format.ISODateTimeFormat
import org.joda.time.DateTimeZone.UTC
import org.joda.time.{ LocalDate, YearMonth }

private[parser] trait Helpers {
  // Wraps a function with a name
  protected[this] case class NamedFunction[T, V](f: T => V, name: String) extends (T => V) {
    def apply(t: T): V              = f(t)
    override def toString(): String = name
  }

  // A function for valid whitespaces
  protected[this] val Whitespace = NamedFunction(" \r\n".contains(_: Char), "Whitespace")

  // A function for valid digits
  protected[this] val Digits = NamedFunction('0' to '9' contains (_: Char), "Digits")

  // Standard data format
  protected[this] val dateTimeFormatter = ISODateTimeFormat.dateTimeParser().withZone(UTC)

  // A function for parse dates
  protected[this] def parseDateTime(dateTime: String) = DateTime.parse(dateTime, dateTimeFormatter)

  // A function for parse LocalDates
  protected[this] def parseLocalDate(localDate: String): LocalDate =
    LocalDate.parse(localDate)

  // A function for parse YearMonths
  protected[this] def parseYearMonth(yearMonth: String): YearMonth =
    YearMonth.parse(yearMonth)
}

package com.garnercorp.swdvm

import com.github.nscala_time.time.Imports._

trait Dates {
  def date(year: Int, month: Int, day: Int) = dateTime(year, month, day)
  def dateTime(year: Int, month: Int, day: Int, hour: Int = 0, minute: Int = 0, second: Int = 0) =
    new DateTime(year, month, day, hour, minute, second)
  def localDate(year: Int, month: Int, day: Int) = new LocalDate(year, month, day)
}

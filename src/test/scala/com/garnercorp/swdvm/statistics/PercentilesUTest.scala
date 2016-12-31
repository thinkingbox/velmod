package com.garnercorp.swdvm.statistics

import com.garnercorp.swdvm.Dates
import org.scalatest.{FunSuite, Matchers}

import scala.collection.immutable.{SortedMap => ImmutableSortedMap}

class PercentilesUTest extends FunSuite with Matchers with Dates {
  test("map time values to the calendar from certain end date") {
    val times = new Percentiles(ImmutableSortedMap(10 -> 0.99, 20 -> 10.2))
    Percentiles.onSchedule(times, date(2015, 8, 15), 3.6) shouldBe
      new Percentiles(ImmutableSortedMap(10 -> dateTime(2015, 8, 12, 9, 22), 20 -> dateTime(2015, 8, 21, 14, 23)))
  }

}

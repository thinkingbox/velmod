package com.garnercorp.swdvm.statistics

import com.garnercorp.swdvm.Dates
import org.scalatest.{FunSuite, Matchers}

import scala.collection.immutable.{SortedMap => ImmutableSortedMap}

class PercentilesUTest extends FunSuite with Matchers with Dates {
  test("map time values backward to the calendar from a certain end date time") {
    val end = dateTime(2015, 8, 15, 12)
    val times = new Percentiles(ImmutableSortedMap(10 -> 0.99, 20 -> 10.2, 30 -> 12.5))
    Percentiles.onSchedule(times, end) shouldBe
      new Percentiles(ImmutableSortedMap(
        10 -> dateTime(2015, 8,  3, 23, 46),
        20 -> dateTime(2015, 8, 13,  4, 48),
        30 -> end
      ))
  }

}

package com.garnercorp.swdvm

import org.scalatest.{Matchers, FunSuite}
import com.github.nscala_time.time.Imports._


class ScheduleUTest extends FunSuite with Matchers with Dates {
  val Start = date(2015, 8, 20)
  test("when there are no tasks delivery is immediate") {
    Schedule(Start, CompositeElapsed()).end shouldBe Start
  }
  test("map task deliveries based on a starting date") {
    val projectRun = CompositeElapsed(Elapsed(1.0), Elapsed(2.0))
    Schedule(Start, projectRun).end shouldBe Start + 3.days
  }
  test("delivery maps task runs in order from a starting date") {
    val projectRun = CompositeElapsed(Elapsed(0.5), Elapsed(2.0), Elapsed(3.0))
    Schedule(Start, projectRun).delivery shouldBe Vector(
      ScheduleEntry(dateTime(2015, 8, 20, 12), Elapsed(0.5)),
      ScheduleEntry(dateTime(2015, 8, 22, 12), Elapsed(2.0)),
      ScheduleEntry(dateTime(2015, 8, 25, 12), Elapsed(3.0))
    )
  }
}

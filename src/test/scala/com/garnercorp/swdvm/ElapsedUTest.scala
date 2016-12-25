package com.garnercorp.swdvm

import org.scalatest.{FunSuite, Matchers}

class ElapsedUTest extends FunSuite with Matchers with Dates {
  test("complete from the start after elapsed minutes") {
    Elapsed(0.125).completionFrom(dateTime(2016, 12, 24, 22)) shouldBe dateTime(2016, 12, 25, 1)
  }
}

class CompositeElapsedUTest extends FunSuite with Matchers with Dates {
  test("time sums up all composed individual times") {
    CompositeElapsed().time shouldBe 0.0
    CompositeElapsed(Elapsed(1.2), Elapsed(1.3)).time shouldBe 2.5
  }
}

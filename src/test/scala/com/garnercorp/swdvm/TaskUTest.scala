package com.garnercorp.swdvm

import org.scalatest.{Matchers, FunSuite}

class TaskUTest extends FunSuite with Matchers {
  test("estimate 0 means that it takes 0 days of work") {
    Task("foo", 0.0).workRequired() shouldBe 0.0
  }
  test("work required is affected by optimism factor when specified") {
    Task("foo", 1.0).workRequired(factor = 2.0) shouldBe 2.0
  }
  test("work required is affected by random variation when specified") {
    Task("foo", 1.0).workRequired(variation = RandomVariation.always(2.0)) shouldBe 2.0
  }
}

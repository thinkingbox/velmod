package com.garnercorp.swdvm

import org.scalatest.{Matchers, FunSuite}

class Strategy1UTest extends FunSuite with Matchers {
  val Strategy = Strategy1(Task.empty, 3, 6.0)
  test("strategy1: time, work actual & velocity are calculated directly from work required & programmers") {
    Strategy.time shouldBe 2.0
    Strategy.workActual shouldBe 6.0
    Strategy.velocity shouldBe 3
  }
  test("strategy1: there is never accumulation of tech debt") {
    Strategy.techDebt shouldBe 0.0
  }
  test("strategy1: run ignores previous tasks and takes a snapshot with factor and random variation") {
    val ignoredPreviousTaskRun = TaskRun(-42, 42)
    val task = Task(20)
    val Programmers = 2000
    Strategy1.run(ignoredPreviousTaskRun, task)(Programmers, 3, RandomVariation.always(0.2)) shouldBe
      Strategy1(task, Programmers, 12)
  }
}

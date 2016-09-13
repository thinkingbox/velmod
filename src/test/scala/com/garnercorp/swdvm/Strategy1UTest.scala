package com.garnercorp.swdvm

import org.scalatest.{Matchers, FunSuite}

class Strategy1UTest extends FunSuite with Matchers {
  test("strategy1: given work required & programmers calculate time, work actual, tech debt & velocity") {
    val taskRun = Strategy1(Task.empty, 3, 6.0)
    taskRun.programmers shouldBe 3
    taskRun.time shouldBe 2.0
    taskRun.workActual shouldBe 6.0
    taskRun.techDebt shouldBe 0.0
    taskRun.velocity shouldBe 3
  }
}

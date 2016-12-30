package com.garnercorp.swdvm

import org.scalatest.mock.EasyMockSugar
import org.scalatest.{FunSuite, Matchers}

class Strategy2UTest extends FunSuite with Matchers with EasyMockSugar {
  val Always_1 = RandomVariation.always(1)
  test("if previous velocity is 0 it takes an infinite time and no other quantity can be calculated") {
    val taskRun = Strategy2(TaskRun(0, 0),
      Task.empty,
      desiredTime = 5.0,
      workRequired = 12.0,
      programmers = 2,
      deathTechDebt = 100.0)
    taskRun.time shouldBe Double.PositiveInfinity
    taskRun.workActual.isNaN shouldBe true
    taskRun.techDebt.isNaN shouldBe true
    taskRun.velocity.isNaN shouldBe true
  }
  test("if desired time < required time and previously no balance, satisfy request and accumulate tech debt") {
    val previous = TaskRun(1.5, 7)
    val DesiredTime = 5.0
    val taskRun = Strategy2(previous,
      Task.empty,
      desiredTime = DesiredTime,
      workRequired = 12.0,
      programmers = 2,
      deathTechDebt = 100.0)
    taskRun.time shouldBe DesiredTime
    taskRun.techDebt shouldBe previous.techDebt + 4.5
  }
  test("if desired time < required time and previously there is a small balance (smaller than the difference), " +
      "satisfy request absorbing whole balance and accumulate less tech debt") {
    val previous = TaskRun(1.5, 7, 2)
    val DesiredTime = 5.0
    val taskRun = Strategy2(previous,
      Task.empty,
      desiredTime = DesiredTime,
      workRequired = 12.0,
      programmers = 2,
      deathTechDebt = 100.0)
    taskRun.time shouldBe DesiredTime + previous.balance
    taskRun.techDebt shouldBe previous.techDebt + 1.5
    taskRun.balance shouldBe 0
  }
  test("if desired time < required time and previously there is a big balance (bigger than the difference), " +
    "satisfy request absorbing part of balance but accumulate no tech debt") {
    val previous = TaskRun(1.5, 7, 10)
    val DesiredTime = 5.0
    val taskRun = Strategy2(previous,
      Task.empty,
      desiredTime = DesiredTime,
      workRequired = 12.0,
      programmers = 2,
      deathTechDebt = 100.0)
    taskRun.time shouldBe DesiredTime + 3
    taskRun.techDebt shouldBe previous.techDebt
    taskRun.balance shouldBe previous.balance - 3
  }
  test("if desired time >= required time and previously there is tech debt and we are already late, " +
    "absorb time to reduce lateness and leave tech debt untouched") {
    val previous = TaskRun(1.5, 7, -20)
    val DesiredTime = 10.0
    val taskRun = Strategy2(previous,
      Task.empty,
      desiredTime = DesiredTime,
      workRequired = 12.0,
      programmers = 2,
      deathTechDebt = 100.0)
    taskRun.time shouldBe DesiredTime - 2
    taskRun.techDebt shouldBe previous.techDebt
    taskRun.balance shouldBe previous.balance + 2
  }
  test("if desired time >= required time and previously there is tech debt but we are early, " +
    "tech debt reduces with an efficiency factor and we are not earlier") {
    val previous = TaskRun(1.5, 7, 2)
    val DesiredTime = 10.0
    val taskRun = Strategy2(previous,
      Task.empty,
      desiredTime = DesiredTime,
      workRequired = 12.0,
      programmers = 2,
      deathTechDebt = 100.0,
      efficiencyTechDebtReduction = 0.75)
    taskRun.time shouldBe DesiredTime
    taskRun.techDebt shouldBe previous.techDebt - 2.25
    taskRun.balance shouldBe previous.balance
  }
  test("actual work (and equivalently time) cannot be less than 25% of work (time) required") {
    val taskRun = Strategy2(TaskRun(2, 0),
      Task.empty,
      desiredTime = 1.0,
      workRequired = 10.0,
      programmers = 2,
      deathTechDebt = 100.0)
    taskRun.time shouldBe 1.25
    taskRun.workActual shouldBe 2.5
  }
  test("velocity cannot become negative") {
    val taskRun = Strategy2(TaskRun(2, 0),
      Task.empty,
      desiredTime = 1.0,
      workRequired = 1000.0,
      programmers = 2,
      deathTechDebt = 100.0)
    taskRun.velocity shouldBe 0
  }
  test("work actual is calculated based on previous velocity") {
    val previous = TaskRun(1.5, 7)
    val taskRun = Strategy2(previous,
                            Task.empty,
                            desiredTime = 5.0,
                            workRequired = 12.0,
                            programmers = 2,
                            deathTechDebt = 100.0)
    taskRun.workActual shouldBe 7.5
    taskRun.velocity shouldBe 1.77
  }
  test("tech debt cannot become negative and velocity cannot increase the ideal value (# of programmers)") {
    val taskRun = Strategy2(TaskRun(2, 1),
                            Task.empty,
                            desiredTime = 10.0,
                            workRequired = 12.0,
                            programmers = 2,
                            deathTechDebt = 100.0,
                            efficiencyTechDebtReduction = 0.75)
    taskRun.time shouldBe 10.0
    taskRun.techDebt shouldBe 0
    taskRun.velocity shouldBe 2
  }
  test("when running, if previous run reaches velocity 0 the next run is dead") {
    val deathTechDebt = 100
    val previous = TaskRun(0, deathTechDebt)
    val next = Strategy2.run(Map.empty[Task, Double], deathTechDebt)(previous, Task.empty)(2, 1, Always_1)
    next.velocity shouldBe 0
    next.time shouldBe 0
  }
  test("when running, next run uses snapshot data from previous run, without recalculating") {
    val previous = mock[TaskRun]
    previous.snapshot.andReturn(TaskRun(2, 50)).anyTimes()
    previous.velocity.andReturn(1).times(1).andThrow(new RuntimeException).anyTimes()
    whenExecuting(previous) {
      val next = Strategy2.run(Map(Task(4) -> 1), 100)(previous, Task(4))(2, 1, Always_1)
      next.programmers shouldBe 2
      next.workRequired shouldBe 4
      next.workActual shouldBe 2
      next.velocity shouldBe 0.96
      next.techDebt shouldBe 52
    }
  }
}

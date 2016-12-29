package com.garnercorp.swdvm

import org.scalatest.mock.EasyMockSugar
import org.scalatest.{FunSuite, Matchers}

class Strategy2UTest extends FunSuite with Matchers with EasyMockSugar {
  test("it takes an infinite time if previous velocity is 0 and no other quantity can be calculated") {
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
  test("given a previous task run with a certain velocity & tech debt, " +
      "time, work required, programmers and death tech debt, " +
      "calculate work actual, tech debt & velocity") {
    val taskRun = Strategy2(TaskRun(1.5, 7),
                            Task.empty,
                            desiredTime = 5.0,
                            workRequired = 12.0,
                            programmers = 2,
                            deathTechDebt = 100.0)
    taskRun.programmers shouldBe 2
    taskRun.time shouldBe 5.0
    taskRun.workActual shouldBe 7.5
    taskRun.techDebt shouldBe 11.5
    taskRun.velocity shouldBe 1.77
  }
  test("when time > work required / velocity and we are on time or early, " +
       "tech debt reduces with an efficiency factor") {
    val taskRun = Strategy2(TaskRun(1.5, 7),
                            Task.empty,
                            desiredTime = 10.0,
                            workRequired = 12.0,
                            programmers = 2,
                            deathTechDebt = 100.0,
                            efficiencyTechDebtReduction = 0.75)
    taskRun.time shouldBe 10.0
    taskRun.workActual shouldBe 15.0
    taskRun.techDebt shouldBe 4.75
    taskRun.velocity shouldBe 1.905
    taskRun.balance shouldBe 0
  }
  test("when time > work required / velocity, but there's no tech debt, " +
       "then work actual = work required and balance is positive") {
    val taskRun = Strategy2(TaskRun(2, 0),
                            Task.empty,
                            desiredTime = 10.0,
                            workRequired = 12.0,
                            programmers = 2,
                            deathTechDebt = 100.0)
    taskRun.time shouldBe 6.0
    taskRun.workActual shouldBe 12.0
    taskRun.techDebt shouldBe 0
    taskRun.velocity shouldBe 2
    taskRun.balance shouldBe 4
  }
  test("when time > work required / velocity and we are late, " +
      "time saved goes on being more on time first, rather than recovering tech debt") {
    val taskRun = Strategy2(TaskRun(1.5, 7, -2),
                            Task.empty,
                            desiredTime = 11.0,
                            workRequired = 12.0,
                            programmers = 2,
                            deathTechDebt = 100.0,
                            efficiencyTechDebtReduction = 0.75)
    taskRun.time shouldBe 9
    taskRun.workActual shouldBe 13.5
    taskRun.techDebt shouldBe 5.875
    taskRun.velocity shouldBe 1.8825
    taskRun.balance shouldBe 0
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
    taskRun.workActual shouldBe 20.0
    taskRun.techDebt shouldBe 0
    taskRun.velocity shouldBe 2
  }
  test("actual work cannot be less than 25% of work required") {
    val taskRun = Strategy2(TaskRun(2, 0),
                            Task.empty,
                            desiredTime = 1.0,
                            workRequired = 10.0,
                            programmers = 2,
                            deathTechDebt = 100.0)
    taskRun.time shouldBe 1.25
    taskRun.workActual shouldBe 2.5
    taskRun.techDebt shouldBe 7.5
    taskRun.velocity shouldBe 1.85
  }
  test("velocity cannot become negative") {
    val taskRun = Strategy2(TaskRun(2, 0),
                            Task.empty,
                            desiredTime = 1.0,
                            workRequired = 1000.0,
                            programmers = 2,
                            deathTechDebt = 100.0)
    taskRun.time shouldBe 125
    taskRun.workActual shouldBe 250
    taskRun.techDebt shouldBe 750
    taskRun.velocity shouldBe 0
  }
  test("when running, if previous run reaches velocity 0 the next run is dead") {
    val next = Strategy2.run(Map.empty[Task, Double], 100)(TaskRun(0, 100), Task.empty)(2, 1, RandomVariation.always(1))
    next.velocity shouldBe 0
    next.time shouldBe 0
  }
  test("when running, next run uses snapshot data from previous run, without recalculating") {
    val previous = mock[TaskRun]
    previous.snapshot.andReturn(TaskRun(2, 50)).anyTimes()
    previous.velocity.andReturn(1).times(1).andThrow(new RuntimeException).anyTimes()
    whenExecuting(previous) {
      val next = Strategy2.run(Map(Task(4) -> 1), 100)(previous, Task(4))(2, 1, RandomVariation.always(1))
      next.programmers shouldBe 2
      next.workRequired shouldBe 4
      next.workActual shouldBe 2
      next.velocity shouldBe 0.96
      next.techDebt shouldBe 52
    }
  }
  test("if we are early with the previous task and the next one requires more work than desired, " +
      "reduce balance but tech debt is unaffected if balance still positive") {
    val taskRun1 = Strategy2(TaskRun(2, 0),
                             Task.empty,
                             desiredTime = 50.0,
                             workRequired = 29.8,
                             programmers = 2,
                             deathTechDebt = 100.0)
    taskRun1.balance shouldBe 35.1

    val taskRun2 = Strategy2(taskRun1.snapshot,
                             Task.empty,
                             desiredTime = 50.0,
                             workRequired = 35.9,
                             programmers = 2,
                             deathTechDebt = 100.0)
    taskRun2.balance shouldBe 67.15
    taskRun2.workActual shouldBe 35.9

    val taskRun3 = Strategy2(taskRun2.snapshot,
                             Task.empty,
                             desiredTime = 20.0,
                             workRequired = 54.0,
                             programmers = 2,
                             deathTechDebt = 100.0)
    taskRun3.balance shouldBe 60.15 +- 0.01
    taskRun3.workActual shouldBe 54
    taskRun3.techDebt shouldBe 0
  }
  test("even if we were early before, constantly underestimating will eventually lead to tech debt, " +
       "consuming all balance and affecting velocity") {
    val taskRun1 = Strategy2(TaskRun(2, 0),
                             Task.empty,
                             desiredTime = 25.0,
                             workRequired = 29.8,
                             programmers = 2,
                             deathTechDebt = 100.0)
    taskRun1.balance shouldBe 10.1
    taskRun1.time shouldBe 14.9
    taskRun1.workActual shouldBe 29.8

    val taskRun2 = Strategy2(taskRun1.snapshot,
                             Task.empty,
                             desiredTime = 25.0,
                             workRequired = 80.4,
                             programmers = 2,
                             deathTechDebt = 100.0)
    taskRun2.balance shouldBe 0.0 +- 0.01
    taskRun2.time shouldBe 35.1
    taskRun2.workActual shouldBe 70.2
    taskRun2.techDebt shouldBe 10.2 +- 0.01
    taskRun2.velocity shouldBe 1.796 +- 0.01
  }
  test("when balance is 0 if next task is desired to be done in less than 25% of time, " +
       "balance goes negative and task is late") {
    val taskRun3 = Strategy2(TaskRun(1.796, 10.2),
                             Task.empty,
                             desiredTime = 10.0,
                             workRequired = 162,
                             programmers = 2,
                             deathTechDebt = 100.0)
    taskRun3.balance shouldBe -12.55 +- 0.01
    taskRun3.time shouldBe 22.55 +- 0.01
    taskRun3.workActual shouldBe 40.5
    taskRun3.techDebt shouldBe 131.7
    taskRun3.velocity shouldBe 0
  }
  test("even when balance is > 0 if next task is desired to be done in less than 25% of time, " +
       "balance can go negative and task is late") {
    val taskRun2 = Strategy2(TaskRun(2, 0, 10.1),
                             Task.empty,
                             desiredTime = 5.0,
                             workRequired = 140,
                             programmers = 2,
                             deathTechDebt = 100.0)
    taskRun2.balance shouldBe -2.4 +- 0.01
    taskRun2.time shouldBe 17.5
    taskRun2.workActual shouldBe 35
    taskRun2.techDebt shouldBe 105
    taskRun2.velocity shouldBe 0
  }
}

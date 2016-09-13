package com.garnercorp.swdvm

import org.scalatest.{Matchers, FunSuite}

class ProjectRunUTest extends FunSuite with Matchers {
  test("project run delivery is the sum of all task runs delivery regardless of how it was determined") {
    ProjectRun(Strategy1(2.0), Strategy1(1.0), Strategy1(3.0), Strategy1(4.0)).time shouldBe 10.0
  }
  test("project run is dead if the last task run has velocity 0") {
    ProjectRun(Strategy1(2.0)) should not be 'dead
    ProjectRun(TaskRun(0, 1)) shouldBe 'dead
    ProjectRun() should not be 'dead
  }
  test("project run with strategy 1 is never late") {
    ProjectRun(Strategy1(2.0)) should not be 'late
    ProjectRun() should not be 'late
  }
  test("project run is late if final balance is negative") {
    val run1 = ProjectRun(TaskRun(1, 0, 1))
    run1 should not be 'late
    run1.slippedTasks shouldBe 0

    val run2 = ProjectRun(TaskRun(1, 0, 1), TaskRun(1, 0, -1))
    run2 shouldBe 'late
    run2.slippedTasks shouldBe 1

    val run3 = ProjectRun(TaskRun(1, 0, 1), TaskRun(1, 0, -1), TaskRun(1, 0, 0))
    run3 should not be 'late
    run3.slippedTasks shouldBe 1
  }
}
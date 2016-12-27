package com.garnercorp.swdvm

import com.garnercorp.swdvm.Project.Strategy
import org.scalatest.{FunSuite, LoneElement, Matchers}

class ProjectUTest extends FunSuite with Matchers with LoneElement {
  val Tasks = Vector(Task(1.0), Task(2.0), Task(3.0))
  val Programmers = 1
  val Factor = 3.0

  test("run delivers every task based on history so far") {
    val summation: Strategy = (previousTaskRun: TaskRun, task: Task) =>
      (programmers: Double, factor: Double, variation: RandomVariation) =>
        TaskRun(task, 42, 42, 42, Programmers, 42, previousTaskRun.time + task.workRequired(), 42, 42)

    val run = Project(Tasks: _*).run(summation, 1, Programmers)
    run.loneElement.taskRuns.map(_.time) shouldBe Vector(1.0, 3.0, 6.0)
  }

  test("single run delivers every task with the specified parameters") {
    val run = Project(Tasks: _*).run(Strategies.One, 1, Programmers, Factor, RandomVariation.always(2.0))
    run.loneElement.taskRuns.map(_.time) shouldBe Vector(6.0, 12.0, 18.0)
  }
  
  test("multiple runs repeat delivering every task with the specified parameters") {
    val run = Project(Tasks: _*).run(Strategies.One, 2, Programmers, Factor, RandomVariation.alternates(2.0, 0.5))
    run.flatMap(_.taskRuns.map(_.time)) shouldBe Vector(6.0, 3.0, 18.0, 1.5, 12.0, 4.5)
  }

}

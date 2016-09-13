package com.garnercorp.swdvm

import com.garnercorp.swdvm.Project.Strategy

case class Project(tasks: Vector[Task]) {
  def run(strategy: Strategy,
          times: Int,
          programmers: Double = 1,
          factor: Double = 1.0,
          variation: RandomVariation = RandomVariation.always(1.0)): Vector[ProjectRun] = {
    def taskRuns = {
      def previousTaskRun(runsSoFar: Vector[TaskRun]): TaskRun =
        runsSoFar.lastOption.getOrElse(TaskRun(programmers, 0))

      tasks.foldLeft(Vector.empty[TaskRun])(
        (runsSoFar, task) => runsSoFar :+ strategy(previousTaskRun(runsSoFar), task)(programmers, factor, variation))
    }

    Vector.tabulate(times)(_ => new ProjectRun(programmers, factor, variation, taskRuns))
  }

}

object Project {
  type Strategy = (TaskRun, Task) => (Double, Double, RandomVariation) => TaskRun
  def apply(tasks: Task*): Project = Project(Vector(tasks: _*))
}

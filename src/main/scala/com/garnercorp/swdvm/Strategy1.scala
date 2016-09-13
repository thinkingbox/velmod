package com.garnercorp.swdvm

case class Strategy1(override val task: Task, override val programmers: Double, override val workRequired: Double)
  extends TaskRun {
  override def time = workRequired / programmers
  override def workActual = workRequired
  override def techDebt = 0
  override def velocity = programmers
  override def desiredTime = time
  override def balance = time
}

object Strategy1 {
  def apply(time: Double, programmers: Double = 1): TaskRun = Strategy1(Task.empty, programmers, programmers * time)

  def run(ignoredPreviousTaskRun: TaskRun, task: Task)(programmers: Double,
                                                       factor: Double,
                                                       variation: RandomVariation): TaskRun = {
    Strategy1(task, programmers, task.workRequired(factor, variation))
  }
}
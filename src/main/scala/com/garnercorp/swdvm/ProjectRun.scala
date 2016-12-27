package com.garnercorp.swdvm

case class ProjectRun(programmers: Double,
                      factor: Double,
                      variation: RandomVariation,
                      taskRuns: Vector[TaskRun]) extends CompositeElapsed[TaskRun] {
  override def composition = taskRuns

  private def lastRun: TaskRun = taskRuns.lastOption.getOrElse(TaskRun(programmers, 0))
  def isDead = finalVelocity == 0
  def isLate = !isDead && finalBalance < 0
  def slippedTasks = taskRuns.count(_.isLate)
  def finalTechDebt = lastRun.techDebt
  def finalVelocity = lastRun.velocity
  def finalBalance = lastRun.balance
}

object ProjectRun {
  def apply(programmers: Double, factor: Double, variation: RandomVariation, taskRuns: TaskRun*): ProjectRun =
    ProjectRun(programmers, factor, variation, Vector(taskRuns: _*))
  def apply(taskRuns: TaskRun*): ProjectRun = apply(1.0, 1.0, RandomVariation.always(1.0), taskRuns: _*)
  def apply(taskRuns: Vector[TaskRun]): ProjectRun = apply(taskRuns: _*)
}



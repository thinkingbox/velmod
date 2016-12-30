package com.garnercorp.swdvm

case class Strategy2(previous: TaskRun,
                     override val task: Task,
                     desiredTime: Double,
                     override val workRequired: Double,
                     override val programmers: Double,
                     deathTechDebt: Double,
                     efficiencyTechDebtReduction: Double = 1.0)
  extends TaskRun {
  override def time = {
    val timeRequired = workRequired / previous.velocity
    val candidate = if (timeRequired > desiredTime) {
      val missingTime = timeRequired - desiredTime
      desiredTime + math.min(previous.balance, missingTime)
    } else if (previous.techDebt > 0 && !previous.isLate)
      desiredTime
    else
      timeRequired
    math.max(candidate, 0.25 * timeRequired)
  }
  override def workActual = time * previous.velocity
  override def techDebt = {
    val delta = workRequired - workActual
    if (delta >= 0)
      previous.techDebt + delta
    else
      math.max(previous.techDebt + efficiencyTechDebtReduction * delta, 0)
  }
  override def velocity = math.max(programmers * (1 - techDebt / deathTechDebt), 0)
  override def balance: Double = desiredTime - time + previous.balance
}

object Strategy2 {
  def apply(_time: Double, _desiredTime: Double): TaskRun = new TaskRun {
    override def task: Task = Task.empty
    override def time: Double = _time
    override def desiredTime: Double = _desiredTime
    override def velocity: Double = 1
    override def workActual: Double = 0
    override def techDebt: Double = 0
    override def programmers: Double = 1
    override def workRequired: Double = 0
    override def balance: Double = time
  }
  def deadRun(previousTaskRun: TaskRun, currentTask: Task) = new TaskRun {
    override def task: Task = currentTask
    override def velocity: Double = 0
    override def workActual: Double = 0
    override def techDebt: Double = previousTaskRun.techDebt
    override def programmers: Double = previousTaskRun.programmers
    override def workRequired: Double = 0
    override def time: Double = 0
    override def desiredTime: Double = 0
    override def balance: Double = 0
  }
  def run(desiredTimes: Map[Task, Double], deathTechDebt: Double)
         (previousTaskRun: TaskRun, task: Task)
         (programmers: Double, factor: Double, variation: RandomVariation): TaskRun =
    if (previousTaskRun.velocity == 0)
      deadRun(previousTaskRun, task)
    else
      Strategy2(previousTaskRun.snapshot,
                task,
                desiredTimes(task),
                task.workRequired(factor, variation),
                programmers,
                deathTechDebt,
                0.5)
}
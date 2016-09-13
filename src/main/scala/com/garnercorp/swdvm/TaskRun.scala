package com.garnercorp.swdvm

trait TaskRun extends Elapsed {
  def task: Task
  def programmers: Double
  def workRequired: Double
  def workActual: Double
  def techDebt: Double
  def velocity: Double
  def desiredTime: Double

  def snapshot = TaskRun(task,
                         velocity,
                         workActual,
                         techDebt,
                         programmers,
                         workRequired,
                         time,
                         desiredTime,
                         balance)

  def isLate = balance < 0
  def balance: Double
}

object TaskRun {
  def apply(_velocity: Double, _techDebt: Double, _balance: Double = 0): TaskRun = new TaskRun {
    override def task: Task = Task.empty
    override def velocity: Double = _velocity
    override def workActual: Double = 0
    override def techDebt: Double = _techDebt
    override def programmers: Double = 0
    override def workRequired: Double = 0
    override def time: Double = 0
    override def desiredTime: Double = 0
    override def balance: Double = _balance
  }

  def apply(_task: Task,
            _velocity: Double,
            _workActual: Double,
            _techDebt: Double,
            _programmers: Double,
            _workRequired: Double,
            _time: Double,
            _desiredTime: Double,
            _balance: Double): TaskRun =
    new TaskRun {
    override def task: Task = _task
    override def velocity: Double = _velocity
    override def workActual: Double = _workActual
    override def techDebt: Double = _techDebt
    override def programmers: Double = _programmers
    override def workRequired: Double = _workRequired
    override def time: Double = _time
    override def desiredTime: Double = _desiredTime
    override def balance: Double = _balance
  }
}
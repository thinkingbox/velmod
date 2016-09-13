package com.garnercorp.swdvm

object Strategies {
  val One: Project.Strategy = Strategy1.run

  def two(desiredTimes: Map[Task, Double], deathTechDebt: Double): Project.Strategy =
    Strategy2.run(desiredTimes, deathTechDebt)
}
package com.garnercorp.swdvm

case class Task(description: String, estimate: Double) {
  def workRequired(factor: Double = 1.0, variation: RandomVariation = RandomVariation.always(1.0)) =
    estimate * factor * variation.next
}

object Task {
  val empty = apply(0.0)
  def apply(estimate: Double): Task = Task("", estimate)
}
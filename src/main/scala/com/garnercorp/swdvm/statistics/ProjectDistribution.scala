package com.garnercorp.swdvm.statistics

import com.garnercorp.swdvm.{CompositeElapsed, Elapsed}

class TaskDistribution[E <: Elapsed](val iterations: Vector[E]) extends MonteCarlo(iterations) {
  override def hashCode = iterations.hashCode()

  override def equals(other: Any) = other != null &&
    other.isInstanceOf[TaskDistribution[E]] &&
    other.asInstanceOf[TaskDistribution[E]].iterations == iterations
}

object TaskDistribution {
  def from[E <: Elapsed](runs: Vector[CompositeElapsed[E]]): Vector[TaskDistribution[E]] =
    runs.map(_.composition).transpose.map(new TaskDistribution(_))
}

class ProjectDistribution[E <: Elapsed](iterations: Vector[TaskDistribution[E]])
  extends CompositeElapsed[TaskDistribution[E]] {
  override def composition: Vector[TaskDistribution[E]] = iterations
}

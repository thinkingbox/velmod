package com.garnercorp.swdvm

import com.github.nscala_time.time.Imports._

trait Elapsed {
  def time: Double
  def completionFrom(start: DateTime) = start + (time*24*60).toInt.minutes
}

object Elapsed {
  private case class PlainElapsed(time: Double) extends Elapsed {
    override def toString = time.toString
  }

  def apply(value: Double): Elapsed = PlainElapsed(value)
}

trait CompositeElapsed[E <: Elapsed] extends Elapsed {
  def composition: Vector[E]
  override def time = composition.map(_.time).sum
}

object CompositeElapsed {
  def apply[E <: Elapsed](values: E*): CompositeElapsed[E] = new CompositeElapsed[E] {
    override def composition: Vector[E] = Vector(values: _*)
  }
}
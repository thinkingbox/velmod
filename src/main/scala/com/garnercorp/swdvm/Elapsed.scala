package com.garnercorp.swdvm

import com.github.nscala_time.time.Imports._

trait Elapsed {
  def time: Double
  def completionFrom(start: DateTime) = start + (time*24*60).toInt.minutes
}

trait CompositeElapsed[E <: Elapsed] extends Elapsed {
  def composition: Vector[E]
  override def time = composition.map(_.time).sum
}

object Elapsed {
  def apply(value: Double): Elapsed = new Elapsed {
    override def time: Double = value

    override def hashCode = value.hashCode()
    override def equals(other: Any) =
      other != null && other.isInstanceOf[Elapsed] && other.asInstanceOf[Elapsed].time == time
    override def toString = value.toString
  }
}

object CompositeElapsed {
  def apply[E <: Elapsed](values: E*): CompositeElapsed[E] = new CompositeElapsed[E] {
    override def composition: Vector[E] = Vector(values: _*)
  }
}
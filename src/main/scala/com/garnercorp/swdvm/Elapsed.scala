package com.garnercorp.swdvm

import com.github.nscala_time.time.Imports._
import org.joda.time.DateTimeConstants._

trait Elapsed {
  def time: Double
  def completionFrom(start: DateTime) = start + (time*MINUTES_PER_DAY).toInt.minutes
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
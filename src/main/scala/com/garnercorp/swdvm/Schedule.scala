package com.garnercorp.swdvm

import com.github.nscala_time.time.Imports._

case class Schedule[E <: Elapsed](start: DateTime, composite: CompositeElapsed[E]) {
  def end: DateTime = composite.completionFrom(start)

  def delivery: Vector[ScheduleEntry[E]] = {
    val elapseds = composite.composition
    Vector.range(1, elapseds.size+1).map(i => {
      val slice = elapseds.slice(0, i)
      ScheduleEntry(CompositeElapsed(slice: _*).completionFrom(start), slice.last)
    })
  }
}

case class ScheduleEntry[E <: Elapsed](date: DateTime, elapsed: E)


package com.garnercorp.swdvm

import com.github.nscala_time.time.Imports._

import scala.collection.immutable.SortedMap

class Percentiles[T](val values: SortedMap[Int, T]) {
  def asString(implicit evaluation: Sample[T]) =
    s"${for ((perc, value) <- values) yield s"$perc%=${evaluation.asString(value)}"}"
  override def hashCode = values.hashCode()
  override def equals(other: Any) =
    other != null && other.isInstanceOf[Percentiles[T]] && other.asInstanceOf[Percentiles[T]].values == values
  override def toString = values.toString
}

object Percentiles {
  def onSchedule(percentiles: Percentiles[Double], end: DateTime, length: Double): Percentiles[DateTime] =
    new Percentiles(percentiles.values.mapValues(time => end - ((length - time)*24*60).toInt.minutes))
}

trait Sample[T] {
  def asString(t: T): String
}

object Sample {
  implicit object DoubleSample extends Sample[Double] {
    override def asString(d: Double): String = f"$d%.2f"
  }
  implicit object LocalDateSample extends Sample[LocalDate] {
    override def asString(d: LocalDate): String = d.toString
  }
  implicit object DateTimeSample extends Sample[DateTime] {
    override def asString(d: DateTime): String = d.toString
  }
}

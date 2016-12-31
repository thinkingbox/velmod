package com.garnercorp.swdvm.statistics

import com.garnercorp.swdvm.Elapsed
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics

import scala.collection.immutable.SortedMap

class MonteCarlo[E <: Elapsed](iterations: Vector[E]) extends Elapsed {
  private val timeStats = new DescriptiveStatistics
  iterations.foreach(run => timeStats.addValue(run.time))

  def avgTime = timeStats.getMean
  override def time = avgTime
  def stddev = timeStats.getStandardDeviation

  def percentiles = new Percentiles(SortedMap.empty[Int, Double] ++
                                    (10 to 100 by 10 map (perc => perc -> timeStats.getPercentile(perc))))

  def minTime = timeStats.getMin
  def maxTime = timeStats.getMax
  def actuals = iterations

  override def toString = {
    f"size=${iterations.size}, avg=${avgTime}%.2f, stddev=${stddev}%.2f, " +
    f"min=${minTime}%.2f, max=${maxTime}%.2f\n" +
    s"\t${percentiles.asString}"
  }
  def take(n: Int) = new MonteCarlo(iterations.take(n))
}

object MonteCarlo {
  def apply[E <: Elapsed](elapsedRuns: E*): MonteCarlo[E] = new MonteCarlo(Vector(elapsedRuns: _*))
}


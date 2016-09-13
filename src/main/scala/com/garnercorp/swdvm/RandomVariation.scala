package com.garnercorp.swdvm

import breeze.stats.distributions.{Rand, RandBasis, ChiSquared}

trait RandomVariation {
  def next: Double
}

object RandomVariation {
  val ChiSquaredWithSeed0 = chiSquared(randBasis = RandBasis.mt0)
  def always(value: Double): RandomVariation = new RandomVariation {
    override def next: Double = value
  }
  def alternates(values: Double*): RandomVariation = new RandomVariation {
    var index = 0
    override def next: Double = {
      val current = values(index)
      index += 1
      if (index >= values.size)
        index = 0
      current
    }
  }
  def chiSquared(mean: Double = 1.5, randBasis: RandBasis = Rand): RandomVariation = new RandomVariation {
    val distribution = new ChiSquared(mean)(randBasis)
    override def next: Double = {
      def nextInRange(lower: Double, upper: Double): Double = {
        val candidate = distribution.draw()
        if (candidate < lower || candidate > upper)
          nextInRange(lower, upper)
        else
          candidate
      }
      val value = nextInRange(0.25, 4)
      accumulated :+= Elapsed(value)
      value
    }
  }
  private var accumulated = Vector.empty[Elapsed]
  def values: MonteCarlo[Elapsed] = new MonteCarlo(accumulated)
  def replay: RandomVariation = alternates(values.actuals.map(_.time): _*)
  def reset() { accumulated = Vector.empty[Elapsed] }
}

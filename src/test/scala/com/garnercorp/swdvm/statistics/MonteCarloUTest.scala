package com.garnercorp.swdvm.statistics

import com.garnercorp.swdvm.{Dates, Elapsed}
import org.scalatest.{FunSuite, Matchers}

import scala.collection.immutable.SortedMap

class MonteCarloUTest extends FunSuite with Matchers with Dates {
  test("calculate average running time and related standard deviation") {
    val montecarlo = MonteCarlo(
      Elapsed(4.0),
      Elapsed(6.0)
    )
    montecarlo.avgTime shouldBe 5
    montecarlo.stddev shouldBe math.sqrt(2) +- 0.01
  }
  test("calculate percentile distribution") {
    val montecarlo = MonteCarlo(
      Elapsed(1.0),
      Elapsed(2.0),
      Elapsed(3.0),
      Elapsed(3.0)
    )
    montecarlo.percentiles shouldBe new Percentiles(SortedMap(10 -> 1.0,
                                                              20 -> 1.0,
                                                              30 -> 1.5,
                                                              40 -> 2.0,
                                                              50 -> 2.5,
                                                              60 -> 3.0,
                                                              70 -> 3.0,
                                                              80 -> 3.0,
                                                              90 -> 3.0,
                                                              100 -> 3.0))
  }
  test("calculate minimum and maximum time") {
    val montecarlo = MonteCarlo(
      Elapsed(4.0),
      Elapsed(6.0),
      Elapsed(7.0),
      Elapsed(4.0)
    )
    montecarlo.minTime shouldBe 4
    montecarlo.maxTime shouldBe 7
  }
}

package com.garnercorp.swdvm

import org.scalatest.{BeforeAndAfter, Matchers, FunSuite}

class RandomVariationUTest extends FunSuite with Matchers with BeforeAndAfter {
  before {
    RandomVariation.reset()
  }

  test("accumulated values are returned for an actual random variation") {
    1 to 1000 foreach { _ => RandomVariation.chiSquared(1.5).next }
    RandomVariation.values.avgTime shouldBe 1.5 +- 0.2
    RandomVariation.values.actuals.size shouldBe 1000
  }
  test("replay returns the last random variation values in a circular way") {
    1 to 10 foreach { _ => RandomVariation.chiSquared(2).next }
    val replay = RandomVariation.replay
    val replayedValues = Vector.tabulate(15)(_ => replay.next)
    val originalValues = RandomVariation.values.actuals.map(_.time)
    replayedValues shouldBe originalValues ++ originalValues.take(5)
  }
}

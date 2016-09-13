package com.garnercorp.swdvm

import org.scalatest.{Matchers, FunSuite}

class ProjectDistributionUTest extends FunSuite with Matchers {
  test("an empty distribution should have no task distribution") {
    new ProjectDistribution(Vector.empty[TaskDistribution[Elapsed]]).composition shouldBe empty
  }
  test("given a single run the distribution collapses to one value for every elapsed") {
    val elapseds = Vector(Elapsed(1), Elapsed(2), Elapsed(3))
    val taskDistributions = elapseds.map(elapsed => new TaskDistribution(Vector(elapsed)))

    val projectDistribution = new ProjectDistribution(taskDistributions)
    projectDistribution.composition.map(_.iterations) shouldBe elapseds.map(Vector(_))
    projectDistribution.composition.map(_.time) shouldBe Vector(1, 2, 3)
    projectDistribution.time shouldBe 6.0
  }
  test("multiple distribution runs build a full distribution with averages for every value") {
    val run1 = Vector(Elapsed(1), Elapsed(2), Elapsed(3))
    val run2 = Vector(Elapsed(4), Elapsed(5), Elapsed(6))
    val distributions = run1.zip(run2).map(pair => Vector(pair._1, pair._2))
    val taskDistributions = distributions.map(new TaskDistribution(_))

    val projectDistribution = new ProjectDistribution(taskDistributions)
    projectDistribution.composition.map(_.iterations) shouldBe distributions
    projectDistribution.composition.map(_.time) shouldBe Vector(2.5, 3.5, 4.5)
    projectDistribution.time shouldBe 10.5
  }
  test("build task distribution") {
    val run1 = CompositeElapsed(Elapsed(1), Elapsed(2), Elapsed(3))
    val run2 = CompositeElapsed(Elapsed(4), Elapsed(5), Elapsed(6))
    val distributions = run1.composition.zip(run2.composition).map(pair => Vector(pair._1, pair._2))
    val taskDistributions = distributions.map(new TaskDistribution(_))
    TaskDistribution.from(Vector(run1, run2)) shouldBe taskDistributions
  }
}


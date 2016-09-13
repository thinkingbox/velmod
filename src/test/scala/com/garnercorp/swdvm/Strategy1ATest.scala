package com.garnercorp.swdvm

import org.scalatest.{FunSuite, Matchers}

class Strategy1ATest extends FunSuite with Matchers with Dates {
  val SampleProject = new Project(EstimateSample.Values.slice(0, 3))
  test("4 devs will take 15 days to complete the project with only 3 tasks, 1 run") {
    val programmers = 4
    val run = SampleProject.run(Strategies.One, 1, programmers).head
    run.time shouldBe 15

    val schedule = Schedule(date(2015, 1, 1), run)
    schedule.end shouldBe date(2015, 1, 16)
    schedule.delivery should contain(ScheduleEntry(date(2015, 1, 16), Strategy1(EstimateSample.Values(2),
                                                                                programmers,
                                                                                10)))
  }

  test("4 devs, with optimistic factor = 2, variation = 3 will take 90 days to complete the first 3 tasks, 1 run") {
    val programmers = 4
    val run = SampleProject.run(Strategies.One, 1, programmers, factor = 2, RandomVariation.always(3)).head
    run.time shouldBe 90

    val schedule = Schedule(date(2015, 1, 1), run)
    schedule.end shouldBe dateTime(2015, 4, 1, 1)
    schedule.delivery should contain(ScheduleEntry(dateTime(2015, 4, 1, 1), Strategy1(EstimateSample.Values(2),
                                                                                      programmers,
                                                                                      60)))
  }

  test("4 devs, increasing variation = 1, 2, 3 will take on avg 30 days to complete first 3 tasks, 3 runs") {
    val programmers = 4
    val runs = SampleProject.run(Strategies.One,
                                 3,
                                 programmers,
                                 variation = RandomVariation.alternates(1, 1, 1, 2, 2, 2, 3, 3, 3))
    val distribution = new ProjectDistribution(TaskDistribution.from(runs))
    val schedule = Schedule(date(2015, 1, 1), distribution)
    schedule.end shouldBe date(2015, 1, 31)
    schedule.delivery should contain(ScheduleEntry(date(2015, 1, 31), new TaskDistribution(Vector(
       Strategy1(EstimateSample.Values(2), programmers, 10),
       Strategy1(EstimateSample.Values(2), programmers, 20),
       Strategy1(EstimateSample.Values(2), programmers, 30)
    ))))
  }
}

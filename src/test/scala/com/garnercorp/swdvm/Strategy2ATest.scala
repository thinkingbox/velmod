package com.garnercorp.swdvm

import org.scalatest.{FunSuite, Matchers}

class Strategy2ATest extends FunSuite with Matchers with Dates {
  val SampleProject = new Project(EstimateSample.Values.slice(0, 3))
  val Task1 = EstimateSample.Values(0)
  val Task2 = EstimateSample.Values(1)
  val Task3 = EstimateSample.Values(2)
  val DeathTechDebt = EstimateSample.Values.slice(0, 3).map(_.estimate).sum * .75

  test("without tech debt, 4 devs will take 15 days to complete the project with only 3 tasks, " +
       "final velocity is 4 like at the beginning, final tech debt is 0, like with strategy 1") {
    val programmers = 4
    val desiredTimes = Map(Task1 -> Task1.estimate / programmers,
                           Task2 -> Task2.estimate / programmers,
                           Task3 -> Task3.estimate / programmers)
    val run = SampleProject.run(Strategies.two(desiredTimes, DeathTechDebt), 1, programmers).head
    run.time shouldBe 15

    val schedule = Schedule(date(2015, 1, 1), run)
    schedule.end shouldBe date(2015, 1, 16)
    val task3ScheduleEntry = schedule.delivery.last
    task3ScheduleEntry.date shouldBe date(2015, 1, 16)
    task3ScheduleEntry.elapsed.task shouldBe Task3
    task3ScheduleEntry.elapsed.workActual shouldBe task3ScheduleEntry.elapsed.workRequired
    task3ScheduleEntry.elapsed.techDebt shouldBe 0
    task3ScheduleEntry.elapsed.velocity shouldBe 4
  }

  test("with a 50% compressed schedule, the project cannot be ended in half time, tech debt accumulates to > 35d, " +
       "final velocity is less than 1") {
    val programmers = 4
    val desiredTimes = Map(Task1 -> Task1.estimate / programmers / 2,
                           Task2 -> Task2.estimate / programmers / 2,
                           Task3 -> Task3.estimate / programmers / 2)
    val run = SampleProject.run(Strategies.two(desiredTimes, DeathTechDebt), 1, programmers).head
    run.time should be > 7.5

    val schedule = Schedule(date(2015, 1, 1), run)
    schedule.end.toLocalDate shouldBe localDate(2015, 1, 8)
    val task3ScheduleEntry = schedule.delivery.last
    task3ScheduleEntry.date.toLocalDate shouldBe localDate(2015, 1, 8)
    task3ScheduleEntry.elapsed.task shouldBe Task3
    task3ScheduleEntry.elapsed.techDebt should be > 35.9
    task3ScheduleEntry.elapsed.velocity should be < 1.0
  }

  test("with a 20% compressed schedule, the project cannot be extended because tech debt = death tech debt, " +
       "and final velocity is 0") {
    val programmers = 4
    val desiredTimes = Map(Task1 -> Task1.estimate / programmers * .2,
                           Task2 -> Task2.estimate / programmers * .2,
                           Task3 -> Task3.estimate / programmers * .2)
    val run = SampleProject.run(Strategies.two(desiredTimes, DeathTechDebt), 1, programmers).head
    run.time should be > 7.5

    val schedule = Schedule(date(2015, 1, 1), run)
    schedule.end.toLocalDate shouldBe localDate(2015, 1, 8)
    val task3ScheduleEntry = schedule.delivery.last
    task3ScheduleEntry.date.toLocalDate shouldBe localDate(2015, 1, 8)
    task3ScheduleEntry.elapsed.task shouldBe Task3
    task3ScheduleEntry.elapsed.techDebt shouldBe DeathTechDebt
    task3ScheduleEntry.elapsed.velocity shouldBe 0
  }

  test("when early in the schedule, even if the next task takes more time than expected don't incur in tech debt") {
    val programmers = 2
    val desiredTimes = Map(Task1 -> Task1.estimate * 2,
                           Task2 -> Task2.estimate * 2,
                           Task3 -> Task3.estimate * 2)
    val run = SampleProject.run(Strategies.two(desiredTimes, DeathTechDebt),
                                1,
                                programmers,
                                variation = RandomVariation.alternates(1.5, 2, 6)).head

    val schedule = Schedule(date(2015, 1, 1), run)
    val task3ScheduleEntry = schedule.delivery.last
    task3ScheduleEntry.elapsed.task shouldBe Task3
    task3ScheduleEntry.elapsed.techDebt shouldBe 0.0
    task3ScheduleEntry.elapsed.velocity shouldBe 2

    schedule.delivery.map(_.elapsed.balance) shouldBe Vector(31.25, 56.25, 46.25)
    schedule.delivery.map(_.date) shouldBe Vector(dateTime(2015, 1, 19, 18),
                                                  dateTime(2015, 2, 13, 18),
                                                  dateTime(2015, 3, 15, 19))
  }
}

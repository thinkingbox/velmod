import com.garnercorp.swdvm._
import com.garnercorp.swdvm.statistics.{MonteCarlo, Percentiles, ProjectDistribution, TaskDistribution}
import com.github.nscala_time.time.Imports._

object Runs extends App with Dates {
  val programmers = 2
  val sampleProject = Project(EstimateSample.Values)
  val printDetails = true
  val idealWorldProject = Project(EstimateSample.Values).run(Strategies.One, 1, programmers)

  def schedule[E <: TaskRun](run: CompositeElapsed[E]) {
    val schedule = Schedule(date(2015, 8, 15), run)
    println(s"Project starts on ${schedule.start} and ends on ${schedule.end}")
    println(s"Vs ideal world ending on ${
      schedule.start +
      Project(EstimateSample.Values).run(Strategies.One, programmers).head.time.toInt.days
    }")
    schedule.delivery.foreach(entry => {
      println(s"${entry.elapsed.task.description}:")
      println(s"\t- with estimate ${entry.elapsed.task.estimate}")
      println(f"\t- lasts for ${entry.elapsed.time}%.1f days")
      println(s"\t- ends on ${entry.date}")
    })
  }


  val runs1: Vector[ProjectRun] = sampleProject.run(Strategies.One,
                                                    1000,
                                                    programmers,
                                                    1.1,
                                                    RandomVariation.ChiSquaredWithSeed0)

  println(s"RandomVariation distribution: ${RandomVariation.values}")
  println(f"First 24 values: ${RandomVariation.values.take(24).actuals}")
  println(s"Related distribution: ${RandomVariation.values.take(24)}\n")

  println(">>>> STRATEGY1 <<<<")
  val distro1 = new ProjectDistribution(TaskDistribution.from(runs1))
  println(f"distro time is ${distro1.time}%.1f vs ${idealWorldProject.head.time}%.1f ideally")
  val montecarlo1 = new MonteCarlo(runs1)
  println(s"distribution is $montecarlo1")
  def printDistroSchedule(distro: ProjectDistribution[TaskRun]) {
    val distroSchedule = Schedule(date(2015, 8, 15), distro)
    println(s"Project starts on ${distroSchedule.start} and ends on ${distroSchedule.end} " +
            s"Vs ideal world ending on ${distroSchedule.start + idealWorldProject.head.time.toInt.days}\n")
    distroSchedule.delivery.foreach(entry => {
      if (printDetails) {
        val task = entry.elapsed.iterations.head.task
        println(s"$task:")
        println(s" - ends on ${entry.date} on avg")
        println(s" - distribution ${entry.elapsed.asInstanceOf[MonteCarlo[_]]}")
        import com.garnercorp.swdvm.statistics.Sample._
        println(s" - dates ${
          Percentiles.onSchedule(entry.elapsed.percentiles, entry.date, entry.elapsed.time).asString
        }")
      }
    })
  }
  printDistroSchedule(distro1)

  def printProjecRun(run: ProjectRun, desiredTimes: Map[Task, Double]) {
    if (printDetails) {
      for (tr <- run.composition) {
        println(f"${tr.task} req=${tr.workRequired}%.1f act=${tr.workActual}%.1f")
        println(f"\t- desired=${desiredTimes(tr.task)}%.1f time=${tr.time}%.1f")
        println(f"\t- tech debt=${tr.techDebt}%.1f velocity=${tr.velocity}%.1f")
        println(f"\t- balance=${tr.balance}%.1f")
      }
    }
    println(s"project is ${if (run.isDead) "" else "not "}dead")
    println(s"project is ${if (run.isLate) "" else "not "}late and has ${run.slippedTasks} late tasks")
    println(s"final tech debt is ${run.finalTechDebt} and final velocity is ${run.finalVelocity}\n")
  }

  println(">>>> STRATEGY2 <<<<")
  val equalDesiredTimes = EstimateSample.Values.map(task => task -> task.estimate / programmers).toMap
  println(s"Equal desired times: $equalDesiredTimes")
  val oneAndHalfDesiredTimes = EstimateSample.Values.map(task => task -> task.estimate / programmers * 1.5).toMap
  println(s"1.5 desired times: $oneAndHalfDesiredTimes")
  val doubleDesiredTimes = EstimateSample.Values.map(task => task -> task.estimate / programmers * 2).toMap
  println(s"Double desired times: $doubleDesiredTimes")
  val tripleDesiredTimes = EstimateSample.Values.map(task => task -> task.estimate / programmers * 3).toMap
  println(s"Triple desired times: $tripleDesiredTimes")
  val techDebtDeath = equalDesiredTimes.values.sum * 1.1 * .75
  println(f"techDebtDeath=$techDebtDeath%.1f\n")

  def printStrategy2(runs: Vector[ProjectRun]) {
    val goodRuns = runs.filterNot(_.isDead)
    val distro = new ProjectDistribution(TaskDistribution.from(goodRuns))
    println(f"distro time is ${distro.time}%.1f vs ${idealWorldProject.head.time}%.1f ideally")
    val montecarlo = new MonteCarlo(goodRuns)
    println(s"distribution is $montecarlo")
    printDistroSchedule(distro)
    val montecarloTechDebt = new MonteCarlo(goodRuns.map(run => Elapsed(run.finalTechDebt)))
    println(s">>> final tech debt distribution is $montecarloTechDebt")
    val montecarloVelocity = new MonteCarlo(goodRuns.map(run => Elapsed(run.finalVelocity)))
    println(s">>> final velocity distribution is $montecarloVelocity\n")
  }


  var run = sampleProject.run(Strategies.two(equalDesiredTimes, techDebtDeath),
                              1,
                              programmers,
                              1.1,
                              RandomVariation.replay).head
  println("Running with strategy2 and equal desired times, just one instance")
  printProjecRun(run, equalDesiredTimes)

  run = sampleProject.run(Strategies.two(doubleDesiredTimes, techDebtDeath),
                          1,
                          programmers,
                          1.1,
                          RandomVariation.replay).head
  println("Running with strategy2 and double desired times, just one instance")
  printProjecRun(run, doubleDesiredTimes)

  run = sampleProject.run(Strategies.two(oneAndHalfDesiredTimes, techDebtDeath),
                          1,
                          programmers,
                          1.1,
                          RandomVariation.replay).head
  println("Running with strategy2 and 1.5 desired times, just one instance")
  printProjecRun(run, oneAndHalfDesiredTimes)

  run = sampleProject.run(Strategies.two(tripleDesiredTimes, techDebtDeath),
                          1,
                          programmers,
                          1.1,
                          RandomVariation.replay).head
  println("Running with strategy2 and triple desired times, just one instance")
  printProjecRun(run, tripleDesiredTimes)

  val runs2_equal: Vector[ProjectRun] = sampleProject.run(Strategies.two(equalDesiredTimes, techDebtDeath),
                                                          1000,
                                                          programmers,
                                                          1.1,
                                                          RandomVariation.replay)
  println(s"${runs2_equal.count(_.isDead)} runs are dead with equalDesiredTimes")
  println(s"${runs2_equal.count(_.isLate)} runs are late with equalDesiredTimes but ${
    runs2_equal.filterNot(_.isDead).filterNot(_.isLate).count(_.slippedTasks > 0) } have late tasks")
  printStrategy2(runs2_equal)

  val runs2_1_5: Vector[ProjectRun] = sampleProject.run(Strategies.two(oneAndHalfDesiredTimes, techDebtDeath),
                                                          1000,
                                                          programmers,
                                                          1.1,
                                                          RandomVariation.replay)
  println(s"${runs2_1_5.count(_.isDead)} runs are dead with 1.5 DesiredTimes")
  println(s"${runs2_1_5.count(_.isLate)} runs are late with 1.5 DesiredTimes but ${
    runs2_1_5.filterNot(_.isDead).filterNot(_.isLate).count(_.slippedTasks > 0) } have late tasks")
  printStrategy2(runs2_1_5)

  val runs2_double: Vector[ProjectRun] = sampleProject.run(Strategies.two(doubleDesiredTimes, techDebtDeath),
                                                           1000,
                                                           programmers,
                                                           1.1,
                                                           RandomVariation.replay)
  println(s"${runs2_double.count(_.isDead)} runs are dead with doubleDesiredTimes")
  println(s"${runs2_double.count(_.isLate)} runs are late with doubleDesiredTimes but ${
    runs2_double.filterNot(_.isDead).filterNot(_.isLate).count(_.slippedTasks > 0) } have late tasks")
  printStrategy2(runs2_double)

  val runs2_triple: Vector[ProjectRun] = sampleProject.run(Strategies.two(tripleDesiredTimes, techDebtDeath),
                                                           1000,
                                                           programmers,
                                                           1.1,
                                                           RandomVariation.replay)
  println(s"${runs2_triple.count(_.isDead)} runs are dead with tripleDesiredTimes")
  println(s"${runs2_triple.count(_.isLate)} runs are late with tripleDesiredTimes but ${
    runs2_triple.filterNot(_.isDead).filterNot(_.isLate).count(_.slippedTasks > 0) } have late tasks")
  printStrategy2(runs2_triple)
}



import com.garnercorp.swdvm._
import com.github.nscala_time.time.Imports._
import Sample._

object CLT extends App {

  println(s"Non randomized values: ${EstimateSample.Values.map(task => Elapsed(task.estimate * 1.1 / 2.0))}")
  val random = RandomVariation.ChiSquaredWithSeed0
  val runs = Vector.tabulate(1000)(_ => EstimateSample.Values.map(task => Elapsed(task.estimate *
                                                                                  1.1 /
                                                                                  2.0 *
                                                                                  random.next)))
  println(s"RandomVariation distribution: ${RandomVariation.values}")
  println(f"First 24 values: ${RandomVariation.values.take(24).actuals}")
  println(s"Related distribution: ${RandomVariation.values.take(24)}\n")
  val start = new DateTime(2015, 8, 15, 0, 0, 0)
  val dates = runs.map(v => for (i <- 1 to v.size) yield {
    val slice = v.slice(0, i).map(_.time)
    val totalTime = slice.sum
    start + (totalTime * 24 * 60).toInt.minutes
  })
  val effortDistro = runs.transpose.map(v => new MonteCarlo(v))
  println(effortDistro)
  (new DateTime(2015, 1, 1, 0, 0, 0) to new DateTime(2015, 1, 15, 0, 0, 0)).toDurationMillis
  new DateTime((new DateTime(2015, 1, 1, 0, 0, 0).getMillis / 1000 / 60) * 60 * 1000)

  val datesDistro = dates.transpose.map(v => v.map(_.getMillis / 1000 / 60).map(Elapsed(_))).map(v => new MonteCarlo(v))
  println(datesDistro)
  val datesDistroWithDates = datesDistro.map(m => new Percentiles(m.percentiles.values.mapValues(time => new
      DateTime((time * 60 * 1000).toLong))))

  println(datesDistroWithDates.map(_.asString).mkString("\n=>"))
  println("\n\nUp to here is the old spike, showing the distribution on the calendar and how more spread it is compared to")
  println("what runs calculate. What follows is a simplified demo.\n\n")
  
  val rand = new util.Random(0)
  val taskNum = 1000
  val iterationsNum = 50
  
  val iterations = Vector.tabulate(iterationsNum)(_ => Vector.tabulate(taskNum)(_ => 1.0 + rand.nextInt(10)))
  
  def distros(in: Vector[Vector[Double]]): Vector[MonteCarlo[Elapsed]] = in.map(vector => new MonteCarlo(vector.map(e => Elapsed(e))))
  def singleDistro(in: Vector[Double]): MonteCarlo[Elapsed] = new MonteCarlo(in.map(e => Elapsed(e)))
  def partialSums(in: Vector[Double]): Vector[Double] = Vector.tabulate(in.size)(i => in.slice(0, i+1).sum)
  
  println(s"iterationRuns = ${iterations.mkString("\n")}")
  println(s"iterationRuns distros = ${distros(iterations).mkString("\n")}")
  val durations = iterations.map(it => it.sum)
  println(s"durations = $durations")
  println(s"durations distro = ${singleDistro(durations)}")
  println(s"avg duration = ${durations.sum/durations.size}")
  
  val partialDurations = iterations.map(partialSums(_))
  println(s"partialDurations = ${partialDurations.mkString("\n")}")
  println(s"partialDurations distros = ${distros(partialDurations).mkString("\n")}")

  val taskDurations = partialDurations.transpose
  println(s"taskDurations = ${taskDurations.mkString("\n")}")
  println(s"taskDurations distros = ${distros(taskDurations).mkString("\n")}")
  
  val projectCompletion = taskDurations.last
  println(s"projectCompletion = $projectCompletion")
  println(s"projectCompletion distro = ${singleDistro(projectCompletion)}")
  
  val taskAverages = iterations.transpose.map(samples => samples.sum/samples.size)
  println(s"taskAverages = $taskAverages")
  println(s"taskAverages distro = ${singleDistro(taskAverages)}")
  
  val wrongProjectCompletion = iterations.transpose.last.map(_ + distros(taskDurations)(taskDurations.size-2).avgTime)
  println(s"wrongProjectCompletion = $wrongProjectCompletion")
  println(s"wrongProjectCompletion distro = ${singleDistro(wrongProjectCompletion)}")
  
  println("\n\nprojectCompletion has the same avg as wrongProjectCompletion but a much bigger stddev (3x+), because of the")
  println("Central Limit Theorem => if we start from an average we have lost all the variability that the stddev captures,")
  println("we have to keep the whole distribution instead. Note that the bigger taskNum the bigger the error.")
  println("With 1000 tasks it's 2.93 vs 81.18 (with the latter being the accurate value)!!")
}

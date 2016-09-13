import com.garnercorp.swdvm._
import com.github.nscala_time.time.Imports._
import Sample._

object CLT extends App {

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

  println(datesDistroWithDates.map(_.asString))
}
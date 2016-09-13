import breeze.stats.DescriptiveStats
import breeze.stats.distributions.{RandBasis, ChiSquared}

//implicit val Seed0 = RandBasis.mt0

val chi = new ChiSquared(2.75)(RandBasis.mt0)
val Count = 1000

for (i <- 1 to Count) println(chi(i))
println(chi.mean)
val draws = for (i <- 1 to Count) yield chi.draw()
println(draws)
println(s"avg: ${draws.sum / draws.size}")

for (p <- Range.Double(0.1, 1.0, 0.1))
  println(s"$p -> ${DescriptiveStats.percentile(draws, p)} ")

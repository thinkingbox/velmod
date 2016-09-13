import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics

val timeStats = new DescriptiveStatistics
for (i <- 1 to 100) timeStats.addValue(i)
for (i <- 40 to 60) timeStats.addValue(i)
for (i <- 45 to 55) timeStats.addValue(i)

println(f"mean: ${timeStats.getMean}%.2f")
println(f"stddev: ${timeStats.getStandardDeviation}%.2f")

10 to 100 by 10 foreach(i => println(f"$i ${timeStats.getPercentile(i)}%.2f"))
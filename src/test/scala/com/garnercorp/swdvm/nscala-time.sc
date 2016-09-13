import com.github.nscala_time.time.Imports._

DateTime.now

DateTime.now.withYear(2015).withMonthOfYear(8).withDayOfMonth(16)

DateTime.now.withDate(2015, 8, 15)

DateTime.now.withDate(new LocalDate(2015, 8, 15))

new LocalDate(2015, 8, 15)
new LocalDate(2015, 8, 15) + 10.hours
new LocalDate(2015, 8, 15) + 61.hours
new LocalDate(2015, 8, 15).interval

new LocalDate(2015, 8, 15) + 10.hours + 15.hours

new LocalDate(2015, 8, 15).toDateTimeAtStartOfDay

DateTime.now.withDate(new LocalDate(2015, 8, 15)).withTime(new LocalTime(0, 0))

new DateTime(2015, 8, 15, 0, 0)
new DateTime(2015, 8, 15, 0, 0) + 10.hours
new DateTime(2015, 8, 15, 0, 0) + 10.hours + 15.hours

new DateTime(2015, 8, 15, 16, 45).toLocalDate
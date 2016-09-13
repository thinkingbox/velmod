name := "SoftwareDevelopmentVelocityModel"

version := "1.0"

scalaVersion := "2.10.5"

libraryDependencies ++= Seq(
  "org.scala-lang" % "scala-reflect" % "2.10.5",
  "org.scalatest" %% "scalatest" % "2.2.4" % "test",
  "org.scalanlp" %% "breeze" % "0.11.2",
  "com.github.nscala-time" %% "nscala-time" % "2.0.0",
  "org.apache.commons" % "commons-math3" % "3.5",
  "org.easymock" % "easymock" % "3.4"
)
# velmod
The purpose of velmod is to build a Software Development Velocity Model that can be used to evaluate different scheduling strategies. The model takes into account aspects like randomly distributed estimation errors and technical debt, considering that when technical debt is too high, a project freezes to death. Different strategies are evaluated on a dataset sample.

# Setup
Make sure that you have the latest sbt on your system:
    SBT: http://www.scala-sbt.org/download.html


## IDE Setup
* IntelliJ - IntelliJ has built in SBT support so when importing just make sure you import the SBT file directly and it will figure everything else out.
* Eclipse - At the time of this writing, Eclipse does not understand SBT natively so make sure you use the `sbt eclipse` command from the root folder to create the necessary eclipse files.


# Usage
All commands below are run from the root folder of the project.

`sbt test` - Will run all tests for the application


ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.3.5"

enablePlugins(ScalafmtPlugin)

enablePlugins(ScoverageSbtPlugin)

lazy val root = (project in file("."))
  .settings(
    name := "PPS-24-SCALcetto",
    libraryDependencies += "com.github.sbt" % "junit-interface" % "0.13.3" % Test,
    // add scala test
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.18" % Test,

    coverageEnabled := true,
  )

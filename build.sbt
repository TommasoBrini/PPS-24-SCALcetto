import scala.sys.process._

ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.3.5"
enablePlugins(ScalafmtPlugin)
enablePlugins(ScoverageSbtPlugin)

scalafmtOnCompile := true

val setupHooks = taskKey[Unit]("Installa i git hooks nella cartella .git/hooks")

setupHooks := {
  val base = baseDirectory.value
  val src = base / "git-hooks"
  val dst = base / ".git" / "hooks"

  val hookFiles = (src ** "*").get.filter(_.isFile)

  val log = streams.value.log

  hookFiles.foreach { file =>
    val target = dst / file.getName
    IO.copyFile(file, target)
    target.setExecutable(true)
    log.info(s"✅ Copiato e reso eseguibile: ${file.getName}")  // uso la val 'log'
  }
}

lazy val root = (project in file("."))
  .settings(
    name := "PPS-24-SCALcetto",
    libraryDependencies += "com.github.sbt" % "junit-interface" % "0.13.3" % Test,
    // add scala test
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.18" % Test,

    coverageEnabled := true,
  )

import play.PlayImport.PlayKeys._

name := "reactivemongo-tailablecursors-demo"

version := "0.11.0"

scalaVersion := "2.11.6"

libraryDependencies ++= Seq(
  "org.reactivemongo" %% "play2-reactivemongo" % "0.11.0.play24",
  "org.specs2" %% "specs2-core" % "2.4.9" % "test",
  "org.specs2" %% "specs2-junit" % "2.4.9" % "test"
)

routesGenerator := InjectedRoutesGenerator

lazy val root = (project in file(".")).enablePlugins(PlayScala)

import play.PlayImport.PlayKeys._

name := "reactivemongo-tailablecursors-demo"

val reactiveMongoVer = "0.11.10"

version := reactiveMongoVer

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  "org.reactivemongo" %% "play2-reactivemongo" % reactiveMongoVer,
  "org.reactivemongo" %% "reactivemongo-iteratees" % reactiveMongoVer
)

libraryDependencies ++= Seq(
  "org.specs2" %% "specs2-core" % "2.4.9" % Test,
  "org.specs2" %% "specs2-junit" % "2.4.9" % Test
)

routesGenerator := InjectedRoutesGenerator

lazy val root = (project in file(".")).enablePlugins(PlayScala)

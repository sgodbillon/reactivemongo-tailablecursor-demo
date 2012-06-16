import sbt._
import Keys._
import PlayProject._

object ApplicationBuild extends Build {

    val appName         = "mongo-async-driver-app"
    val appVersion      = "1.0-SNAPSHOT"

	val appDependencies = Seq(
      "org.asyncmongo" %% "mongo-async-driver" % "0.1-SNAPSHOT"
    )

    val main = PlayProject(appName, appVersion, appDependencies, mainLang = SCALA).settings(
      //resolvers += ("localrepo" at "file:///Users/sgo/.ivy2/local")(Resolver.ivyStylePatterns)
      resolvers += Resolver.file("local repository", file("/Users/sgo/.ivy2/local"))(Resolver.ivyStylePatterns)
    )

}

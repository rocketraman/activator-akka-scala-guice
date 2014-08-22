name := "activator-akka-scala-guice"

version := "1.0-SNAPSHOT"

scalaVersion := "2.11.2"

libraryDependencies ++= {
  val akkaVersion  = "2.3.5"
  Seq(
    "com.typesafe.akka" %% "akka-actor" % akkaVersion,
    "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
    "com.google.inject" % "guice" % "4.0-beta4",
    "net.codingwell" %% "scala-guice" % "4.0.0-beta4",
    "org.scalatest" %% "scalatest" % "2.2.1" % "test",
    "com.typesafe.akka" %% "akka-testkit" % akkaVersion % "test"
  )
}

scalacOptions ++= Seq(
  "-unchecked",
  "-deprecation",
  "-Xlint",
  "-Ywarn-dead-code",
  "-language:_",
  "-target:jvm-1.7",
  "-encoding", "UTF-8"
)

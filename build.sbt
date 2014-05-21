name := "activator-akka-scala-guice"

version := "1.0-SNAPSHOT"

scalaVersion := "2.10.4"

libraryDependencies ++= {
  val akkaVersion  = "2.3.2"
  Seq(
    "com.typesafe.akka" %% "akka-actor" % akkaVersion
      exclude ("org.scala-lang" , "scala-library"),
    "com.typesafe.akka" %% "akka-slf4j" % akkaVersion
      exclude ("org.scala-lang" , "scala-library"),
    "org.scalatest"       %   "scalatest_2.10" % "2.1.7" % "test"
      exclude ("org.scala-lang" , "scala-reflect")
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

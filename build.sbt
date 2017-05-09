name := "introducing-akka-streams"

version := "0.1"

scalaVersion := "2.12.2"

val AkkaVersion = "2.5.1"
val AkkaHttpVersion = "10.0.5"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-stream" % AkkaVersion,
  "com.typesafe.akka" %% "akka-http-core" % AkkaHttpVersion,
  "com.typesafe.akka" %% "akka-http" % AkkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-spray-json" % AkkaHttpVersion,
  "com.typesafe.akka" %% "akka-stream-kafka" % "0.16",

  "org.scalatest"     %% "scalatest" % "3.0.1" % "test",
  "com.typesafe.akka" %% "akka-stream-testkit" % AkkaVersion % "test",
  "com.typesafe.akka" %% "akka-http-testkit" % AkkaHttpVersion % "test"
)

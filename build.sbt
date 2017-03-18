name := """macwire-di"""

version := "1.0-SNAPSHOT"

enablePlugins(PlayScala)

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  ws,
  specs2 % Test,
  "com.softwaremill.macwire" %% "macros" % "1.0.7",
  "com.softwaremill.macwire" %% "runtime" % "1.0.7",
  "com.typesafe.akka" %% "akka-stream-kafka" % "0.14"
)

resolvers += "JBoss" at "https://repository.jboss.org/"

fork in run := true

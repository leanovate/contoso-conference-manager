name := """macwire-di"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  jdbc,
  cache,
  ws,
  specs2 % Test,
  "com.softwaremill.macwire" %% "macros" % "1.0.7",
  "com.softwaremill.macwire" %% "runtime" % "1.0.7",
  "com.typesafe.akka" %% "akka-stream-kafka" % "0.13"
)

resolvers += "JBoss" at "https://repository.jboss.org/"


fork in run := true
javaOptions in run += "-Dhttp.port=9090"

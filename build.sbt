import AssemblyKeys._

name := "VivaldiGSI"

version := "0.1"

scalaVersion := "2.10.0"

resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"

libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.2.3"

libraryDependencies += "com.typesafe.akka" %% "akka-testkit" % "2.2.3"

libraryDependencies += "com.typesafe.akka" %% "akka-slf4j" % "2.2.3"

libraryDependencies += "org.scalatest" % "scalatest_2.10" % "2.0" % "test"

libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.0.13"

libraryDependencies += "net.databinder.dispatch" %% "dispatch-core" % "0.11.0"

libraryDependencies += "net.liftweb" %% "lift-json" % "2.5.1"

lazy val logback = "ch.qos.logback" % "logback-classic" % "1.0.13"

seq(assemblySettings: _*)

mainClass in assembly := Some("org.discovery.vivaldi.Vivaldi")

test in assembly := {}

jarName in assembly := "vivaldi.jar"

mergeStrategy in assembly <<= (mergeStrategy in assembly) { (old) =>
{
  case "application.conf" => MergeStrategy.rename
  case "META-INF/MANIFEST.MF" => old("META-INF/MANIFEST.MF")
  case x => MergeStrategy.first
}
}
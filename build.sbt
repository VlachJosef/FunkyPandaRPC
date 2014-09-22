name := """FunkyPanda RPC"""

version := "0.0.0.1"

organization := "funkypanda"

scalaVersion := "2.11.2"

libraryDependencies ++= Seq(
	"com.google.protobuf" % "protobuf-java" % "2.6.0",
	"net.databinder.dispatch" %% "dispatch-core" % "0.11.2" % "test",
	"net.databinder" %% "unfiltered-directives" % "0.8.1",
	"net.databinder" %% "unfiltered-filter" % "0.8.1",
	"net.databinder" %% "unfiltered-jetty" % "0.8.1",
	"org.sedis" %% "sedis" % "1.2.2",
	"com.typesafe" % "config" % "1.2.1",
	"com.typesafe.scala-logging" %% "scala-logging" % "3.1.0",
	"ch.qos.logback" % "logback-core" % "1.1.2",
	"ch.qos.logback" % "logback-classic" % "1.1.2",
	"org.scalatest" % "scalatest_2.11" % "2.2.1" % "test"
)

resolvers := Seq(
  "Sedis Repo" at "http://pk11-scratch.googlecode.com/svn/trunk"
)

javacOptions ++= Seq("-encoding", "UTF8")

scalacOptions ++= Seq("-feature", "-language:postfixOps")

// Parse source into ASTs, perform simple desugaring
// Can be used to find out which implicit parameters are applied
//scalacOptions in Compile += "-Xprint:typer"

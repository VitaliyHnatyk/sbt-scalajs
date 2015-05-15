import sbtrelease.ReleasePlugin.ReleaseKeys._
import bintray._
import BintrayPlugin.autoImport._

sbtPlugin := true

organization := "com.github.inthenow"

name := "sbt-scalajs"

scalaVersion := "2.10.5"

scalacOptions ++= Seq("-deprecation", "-unchecked")

resolvers ++= Seq(
  Resolver.sbtPluginRepo("releases"),
  Resolver.sonatypeRepo("releases"),
  Resolver.typesafeRepo("releases"),
  Resolver.url("scala-js-releases",
    url("http://bintray.com/scala-js/scala-js-releases"))(Resolver.ivyStylePatterns)
)

libraryDependencies ++= Seq(
  "com.chuusai" %% "shapeless" % "2.2.0-RC6",
  compilerPlugin("org.scalamacros" % "paradise" % "2.0.1" cross CrossVersion.full),
  "junit" % "junit" % "4.12" % "test",
  "org.scalatest" %% "scalatest" % "2.2.4" % "test"
)

addSbtPlugin("org.scala-js" % "sbt-scalajs" % "0.6.3")

addSbtPlugin("com.typesafe.sbt" % "sbt-web" % "1.2.0")

publishMavenStyle := false

publishArtifact in Test := false

bintrayRepository := "sbt-plugins"

licenses += ("Apache-2.0", url("https://www.apache.org/licenses/LICENSE-2.0.html"))

bintrayOrganization := None

releaseSettings




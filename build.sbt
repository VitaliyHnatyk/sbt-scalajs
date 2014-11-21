import sbtrelease.ReleasePlugin.ReleaseKeys._
import bintray.Keys._

sbtPlugin := true

organization := "com.github.inthenow"

name := "sbt-scalajs"

scalaVersion := "2.10.4"

scalacOptions ++= Seq("-deprecation", "-unchecked")

resolvers ++= Seq(
  Resolver.sbtPluginRepo("releases"),
  Resolver.sonatypeRepo("releases"),
  Resolver.typesafeRepo("releases"),
  Resolver.url("scala-js-releases",
    url("http://bintray.com/scala-js/scala-js-releases"))(Resolver.ivyStylePatterns)
)

libraryDependencies ++= Seq(
  "junit" % "junit" % "4.11" % "test"
)

addSbtPlugin("org.scala-lang.modules.scalajs" % "scalajs-sbt-plugin" % "0.5.6")

addSbtPlugin("com.typesafe.sbt" % "sbt-web" % "1.1.0")

//addSbtPlugin("com.typesafe.sbt" % "sbt-js-engine" % "1.0.1")

publishMavenStyle := false

publishArtifact in Test := false

bintrayPublishSettings

repository in bintray := "sbt-plugins"

licenses += ("Apache-2.0", url("https://www.apache.org/licenses/LICENSE-2.0.html"))

bintrayOrganization in bintray := None

releaseSettings




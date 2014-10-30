import sbt._
import Keys._

import com.inthenow.sbt.scalajs._
import SbtScalajs._
import SbtScalajsWeb._
import play.twirl.sbt.Import._

object PlayBuild extends Build {
  import Dependencies._

  val jasmineVersion = "1.3.1"
  val logger = ConsoleLogger()

  lazy val prj = Project(
    id = "app",
    base = file("."),
    settings = defaultSettings ++ scalaDefaultSettings ++ securitySettings ++ Seq(
      name := "scala-js-website",
      TwirlKeys.templateImports += "views.html.appshared._")
  ).enablePlugins(play.PlayScala).dependsOn(security, appShared, ui).aggregate(security, appShared, ui)

  lazy val appShared = Project(
    id = "appshared",
    base = file("modules/appshared"),
    settings = defaultSettings ++ scalaDefaultSettings ++ sjsResources(ui) ++ sjsPlaySettings
  ).enablePlugins(play.PlayScala, SbtScalajsWeb).dependsOn(ui)

  lazy val ui = Project(
    id = "ui",
    base = file("modules/ui"),
    settings = defaultSettings ++ scalajsDefaultSettings
  ).enablePlugins(SbtScalajs)

  lazy val security = Project(
    id = "security",
    base = file("modules/security"),
    settings = defaultSettings ++ scalaDefaultSettings ++ securitySettings
  ).enablePlugins(play.PlayScala).dependsOn(appShared, ui)

  lazy val defaultSettings: Seq[Setting[_]] = Seq(
    organization := "com.github.inthenow",
    version := "0.2",
    scalaVersion := "2.11.2",
    scalacOptions ++= Seq("-deprecation", "-unchecked"),
    publishMavenStyle := false
  )

  lazy val securitySettings: Seq[Setting[_]] = Seq(
    libraryDependencies ++= Seq(
      playPac4j,
      pac4jHttp,
      pac4jCas,
      pac4jOpenid,
      pac4jOauth,
      pac4jSaml,
      playCache
    ),
    resolvers ++= Seq(
      "Sonatype snapshots repository" at "https://oss.sonatype.org/content/repositories/snapshots/",
      "Pablo repo" at "https://raw.github.com/fernandezpablo85/scribe-java/mvn-repo/"
    ),
    TwirlKeys.templateImports += "views.html.appshared._"
  )

  lazy val scalaDefaultSettings: Seq[Setting[_]] = Seq(
    libraryDependencies ++= Seq(
      reactWebjar,
      playWebjar,
      bootstrapWebjar,
      scalatestPlay,
      selenium
    )
  )

  lazy val scalajsDefaultSettings = scalajsJsSettings ++ concatAllSjsDependencies ++  scalaJSJquery
}
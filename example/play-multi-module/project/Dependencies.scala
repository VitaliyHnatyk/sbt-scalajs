import sbt.Keys._
import sbt._

import scala.scalajs.sbtplugin.ScalaJSPlugin._

object Dependencies {
  // Note: %%% can only be used within a task or setting macro, such as :=, +=, ++=, Def.task, or Def.setting...

  // Pac4j
  val playPac4j   = "org.pac4j" % "play-pac4j_scala2.11" % "1.3.0"
  val pac4jHttp   = "org.pac4j" % "pac4j-http"   % "1.6.0"
  val pac4jCas    = "org.pac4j" % "pac4j-cas"    % "1.6.0"
  val pac4jOpenid = "org.pac4j" % "pac4j-openid" % "1.6.0"
  val pac4jOauth  = "org.pac4j" % "pac4j-oauth"  % "1.6.0"
  val pac4jSaml   = "org.pac4j" % "pac4j-saml"   % "1.6.0"

  // Play cache
  val playCache = "com.typesafe.play" % "play-cache_2.11" % "2.3.0"

  // ScalaJS-jquery
  val scalaJSJquery = Seq(libraryDependencies += "org.scala-lang.modules.scalajs" %%% "scalajs-jquery" % "0.6" )

  // Scalatest for play
  val scalatestPlay = "org.scalatestplus" %% "play" % "1.1.0" % "test"

  // Selenium
  val selenium = "org.seleniumhq.selenium" % "selenium-java" % "2.39.0" % "test"

  // Webjars
  val reactWebjar     = "org.webjars" % "react" % "0.8.0"
  val playWebjar      =  "org.webjars" %% "webjars-play" % "2.3.0-2"
  val bootstrapWebjar = "org.webjars" % "bootstrap" % "2.3.2"
}
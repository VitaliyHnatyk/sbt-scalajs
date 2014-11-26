package com.inthenow.sbt.scalajs

import sbt._
import sbt.Keys._

object Import {
  object SbtScalajsKeys {

  }
}

object SbtScalajs extends AutoPlugin {

  override def requires = sbt.plugins.JvmPlugin

  import scala.scalajs.sbtplugin.ScalaJSPlugin.ScalaJSKeys._
  import scala.scalajs.sbtplugin.ScalaJSPlugin._

  val preScalaJSSettings = {
    Seq(
      (crossTarget in fastOptJS) in Compile := (crossTarget in Compile).value / "scalajs_managed" / "js",
      (crossTarget in fastOptJS) in Test := (crossTarget in Test).value / "scalajs_managed" / "js",
      (crossTarget in packageJSDependencies) in Compile := (crossTarget in Compile).value / "scalajs_managed" / "js",
      (crossTarget in packageJSDependencies) in Test := (crossTarget in Test).value / "scalajs_managed" / "js"
    )
  } ++ scalaJSSettings

  val postScalaJSSettings = {
    Seq(
    )
  }

  val compileAutoOpt = Seq(
    ScalaJSKeys.fastOptJS in Compile <<= (ScalaJSKeys.fastOptJS in Compile) dependsOn (compile in Compile) triggeredBy (compile in Compile)
  )

  val testAutoOpt = Seq(
    ScalaJSKeys.fastOptJS in Test <<= (ScalaJSKeys.fastOptJS in Test) dependsOn (fastOptJS in Compile) triggeredBy (compile in Test)
  )

  val autoOpt = compileAutoOpt ++ testAutoOpt

  override def projectSettings =
    preScalaJSSettings ++ postScalaJSSettings ++
      Seq(
      )

  val noPublishSettings = Seq(
    publish := {},
    publishLocal := {},
    packageBin in Compile  := file(""),
    packageDoc in Compile  := file(""),
    packageSrc in Compile  := file("")
  )

  val noRootSettings = noPublishSettings

  def shareDirectories(that: Project, dir: String) = Seq(
    // pseudo link shared files from jvm source/resource directories to js
    unmanagedSourceDirectories in Compile += (baseDirectory in that).value / "src/main/scala" / dir,
    unmanagedSourceDirectories in Test += (baseDirectory in that).value / "src/test/scala" / dir,
    unmanagedResourceDirectories in Compile += (baseDirectory in that).value / "src/main/resources" / dir,
    unmanagedResourceDirectories in Test += (baseDirectory in that).value / "src/test/resources" / dir

  )

  def addDirectories(base: File, dir: String = ".") = Seq(
    // pseudo link shared files from jvm source/resource directories to js
    unmanagedSourceDirectories in Compile += base / "src/main/scala" / dir,
    unmanagedSourceDirectories in Test += base / "src/test/scala" / dir,
    unmanagedResourceDirectories in Compile += base / "src/main/resources" / dir,
    unmanagedResourceDirectories in Test += base / "src/test/resources" / dir

  )

  def linkedSources(sharedSrc: Project) = Seq(
    // pseudo link shared files from jvm source/resource directories to js
    unmanagedSourceDirectories in Compile ++= (unmanagedSourceDirectories in(sharedSrc, Compile)).value,
    unmanagedSourceDirectories in Test ++= (unmanagedSourceDirectories in(sharedSrc, Test)).value,
    unmanagedResourceDirectories in Compile ++= (unmanagedResourceDirectories in(sharedSrc, Compile)).value,
    unmanagedResourceDirectories in Test ++= (unmanagedResourceDirectories in(sharedSrc, Test)).value
  )

  def sjsResources(prjJs: Project) = Seq(
    unmanagedResourceDirectories in Compile += (crossTarget in fastOptJS in Compile in prjJs).value,
    unmanagedResourceDirectories in Test += (crossTarget in fastOptJS in Test in prjJs).value,
    unmanagedResources in Compile += ((packageJSDependencies in Compile) in prjJs).value,
    unmanagedResources in Test += ((packageJSDependencies in Test) in prjJs).value,
    unmanagedResources in Compile += ((artifactPath in fastOptJS in Compile) in prjJs).value,
    unmanagedResources in Test += ((artifactPath in fastOptJS in Compile) in prjJs).value,
    unmanagedResources in Test += ((artifactPath in fastOptJS in Test) in prjJs).value,

    copyResources in Compile <<= (copyResources in Compile) dependsOn (fastOptJS in Compile in prjJs),
    copyResources in Test <<= (copyResources in Test) dependsOn (fastOptJS in Test in prjJs)
  )

  val concatAllSjsDependencies  = Seq(
    skip in ScalaJSKeys.packageJSDependencies := false
  )

  val scalajsJvmSettings = Seq(target := target.value / "jvm")
  val scalajsJsSettings = Seq(target := target.value / "js")
}

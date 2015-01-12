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

  private val scalaJsManaged =   "scalajs_managed/js"


  /**
   * function to create symbolic links to shared folders
   * is not used now but will be in the Future
   * @param dir target folder
   * @param link where we will create link
   * @param rewrite if we should rewrite link if it exists
   * @param log logger to log errors and messages
   * @return
   */
   def createSymbolicLink(dir:File,link:File,rewrite:Boolean = false)(implicit log:Logger) =
    if(link.exists() && !rewrite)
      log.debug(s"detected a link for shared source ${dir.toString}")
    else
    {
      log.debug(s"not detected a link  ${link.toString} for shared source ${dir.toString}, rewrite = ${rewrite}")
      import java.nio.file.Files
      import java.io._
      val newLink = link.toPath
      if(rewrite)Files.deleteIfExists(newLink)
      val folder = dir.toPath
      try {
        Files.createSymbolicLink(newLink, folder)
        log.info(s"symbolic link for shared folder ${folder.toString} has been created!")
      } catch {
        case x: IOException => log.error(x.toString)
        case x: UnsupportedOperationException => log.error(x.toString)
        case other: Throwable => log.error(other.toString)
      }
    }

  val preScalaJSSettings = {
    Seq(
      (crossTarget in fastOptJS) in Compile := (crossTarget in Compile).value / scalaJsManaged,
      (crossTarget in fastOptJS) in Test := (crossTarget in Test).value / scalaJsManaged,
      (crossTarget in packageJSDependencies) in Compile := (crossTarget in Compile).value / scalaJsManaged,
      (crossTarget in packageJSDependencies) in Test := (crossTarget in Test).value / scalaJsManaged
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
    packageBin in Compile := file(""),
    packageDoc in Compile := file(""),
    packageSrc in Compile := file("")
  )

  val noRootSettings = noPublishSettings

  def shareDirectories(that: Project, dir: String) = Seq(
    // pseudo link shared files from jvm source/resource directories to js
    unmanagedSourceDirectories in Compile += (scalaSource in (that,Compile)).value /  dir,
    unmanagedSourceDirectories in Test += (scalaSource in (that,Test)).value  / dir,
    unmanagedResourceDirectories in Compile += (resourceDirectory in (that,Compile)).value / dir,
    unmanagedResourceDirectories in Test += (resourceDirectory in (that,Test)).value / dir

  )

  def addDirectories(base: File, dir: String = ".") = Seq(
    // pseudo link shared files from jvm source/resource directories to js
    unmanagedSourceDirectories in Compile += base / "src/main/scala" / dir,
    unmanagedSourceDirectories in Test += base / "src/test/scala" / dir,
    unmanagedResourceDirectories in Compile += base / "src/main/resources" / dir,
    unmanagedResourceDirectories in Test += base / "src/test/resources" / dir
  )

  def addRelDirectories(projBase: File, relPath: String, dir: String = ".") = {
    val fullPath =  projBase.getCanonicalFile / relPath
    addDirectories(fullPath, dir)
  }

  def linkedSources(sharedSrc: Project) = Seq(
    // pseudo link shared files from jvm source/resource directories to js
    unmanagedSourceDirectories in Compile ++= (unmanagedSourceDirectories in(sharedSrc, Compile)).value,
    unmanagedSourceDirectories in Test ++= (unmanagedSourceDirectories in(sharedSrc, Test)).value,
    unmanagedResourceDirectories in Compile ++= (unmanagedResourceDirectories in(sharedSrc, Compile)).value,
    unmanagedResourceDirectories in Test ++= (unmanagedResourceDirectories in(sharedSrc, Test)).value
  )

  def linkToShared(base:File, pathToShared:String)(implicit log:Logger) = {
    val sharedDir = base.getCanonicalFile / pathToShared
    val sharedSrc = sharedDir / "src/main/scala"
    val sharedTest = sharedDir / "src/main/test"

    val linkMain:File = base.getCanonicalFile / "src/main/scala/shared"
    val linkTest:File = base.getCanonicalFile / "src/test/scala/shared"

    if (sharedSrc.exists()) SbtScalajs.createSymbolicLink(sharedSrc, linkMain)(log)
    else log.debug(s"$sharedSrc does not exist")

    if (sharedTest.exists()) SbtScalajs.createSymbolicLink(sharedTest, linkTest)(log)
    else log.debug(s"$sharedTest does not exist")

    Seq(cleanFiles += linkMain, cleanFiles += linkTest)
  }

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

  val concatAllSjsDependencies = Seq(
    skip in ScalaJSKeys.packageJSDependencies := false
  )

  val scalajsJvmSettings = Seq(target := target.value / "jvm")
  val scalajsJsSettings = Seq(target := target.value / "js")
  val scalajsCommonJsSettings = Seq(target := target.value / "commonjs")
  // Cross Compiler

  val XScalaMacroDependencies: Seq[Setting[_]] =
    Seq(libraryDependencies += "org.scala-lang" % "scala-reflect" % scalaVersion.value,
      libraryDependencies ++= {
        CrossVersion.partialVersion(scalaVersion.value) match {
          // if scala 2.11+ is used, quasiquotes are merged into scala-reflect
          case Some((2, scalaMajor)) if scalaMajor >= 11 => Seq()
          // in Scala 2.10, quasiquotes are provided by macro paradise
          case Some((2, 10)) =>
            Seq(
              compilerPlugin("org.scalamacros" % "paradise" % "2.0.0" cross CrossVersion.full),
              "org.scalamacros" %% "quasiquotes" % "2.0.0" cross CrossVersion.binary
            )
        }
      })

  val XScalaSources: Seq[Setting[_]] = Seq(
    unmanagedSourceDirectories in Compile <+= (sourceDirectory in Compile, scalaBinaryVersion) {
      (s, v) => s / ("scala_" + v)
    },
    unmanagedSourceDirectories in Test <+= (sourceDirectory in Test, scalaBinaryVersion) {
      (s, v) => s / ("scala_" + v)
    }
  )

  val XScalaSettings: Seq[Setting[_]] = XScalaMacroDependencies ++ XScalaSources

}

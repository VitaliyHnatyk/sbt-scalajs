package com.inthenow.sbt.scalajs

//import org.scalajs.sbtplugin.ScalaJSPlugin
import sbt._
import sbt.Keys._

import org.scalajs.sbtplugin._
import org.scalajs.sbtplugin.ScalaJSPlugin.AutoImport._

object Import {

  object SbtScalajsKeys {

  }

}

object SbtScalajs extends AutoPlugin {

  type FileSettings = Def.Setting[Seq[File]]

 /// override def requires = sbt.plugins.JvmPlugin
 override def requires = ScalaJSPlugin

 /// import scala.scalajs.sbtplugin.ScalaJSPlugin.ScalaJSKeys._
 /// import scala.scalajs.sbtplugin.ScalaJSPlugin._
  //import org.scalajs.sbtplugin.ScalaJSPlugin.AutoImport._
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
  } //++ scalaJSSettings

  val postScalaJSSettings = {
    Seq(
    )
  }

  val compileAutoOpt = Seq(
    fastOptJS in Compile <<= (fastOptJS in Compile) dependsOn (compile in Compile) triggeredBy (compile in Compile)
  )

  val testAutoOpt = Seq(
    fastOptJS in Test <<= ( fastOptJS in Test) dependsOn (fastOptJS in Compile) triggeredBy (compile in Test)
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

  def linkedSources(sharedSrc: Project)(implicit log:Logger): Seq[FileSettings] = {
    // pseudo link shared files from jvm source/resource directories to js
    log.debug(s"Creating Sbt links to ${sharedSrc.id}")
    Seq(
      unmanagedSourceDirectories in Compile ++= (unmanagedSourceDirectories in(sharedSrc, Compile)).value,
      unmanagedSourceDirectories in Test ++= (unmanagedSourceDirectories in(sharedSrc, Test)).value,
      unmanagedResourceDirectories in Compile ++= (unmanagedResourceDirectories in(sharedSrc, Compile)).value,
      unmanagedResourceDirectories in Test ++= (unmanagedResourceDirectories in(sharedSrc, Test)).value
    )
  }


  /**
   * Given a base directory, such as the base directory of a project, and a path to a shared directory, creates
   * source links from the shared directory to the project.
   *
   * Example (where ... is the base of the module):
   * Given a project structure
   *
   * {{{.../shared/src/main/scala
   * .../shared/src/main/scala_2.10
   * .../shared/src/main/scala_2.11
   * .../shared/src/test/scala
   *
   * .../js/src/main/scala
   * .../js/src/main/scala_2.10
   * .../js/src/main/scala_2.11}}}
   *
   * Executing
   * {{{linkToShared(".../js", "../shared", "shared")}}}
   * yields the symlinks:
   *
   * {{{.../js/src/main/scala_shared
   * .../js/src/main/scala_shared_2.10
   * .../js/src/main/scala_shared_2.11
   * .../js/src/test/scala_shared}}}
   *
   * @param base target folder where link will be creates
   * @param pathToShared path to the shared folder
   * @param label the name of the shared directory, e.g. "shared", "common", etc.
   * @param log logger to log errors and messages
   * @return a sequence of cleanfile settings so that the build can remove the links via a clean command
   */
  def linkToShared(base: File, pathToShared: String, label: String)(implicit log: Logger): Seq[FileSettings] = {
    // returns list of subdirectories of dir that start with "prefix". Files have full path
    def getSub(dir: File, prefix:String): List[File] = {
      if (dir.exists()) dir.listFiles.filter(_.isDirectory).filter(_.getName.startsWith(prefix)).toList
      else Nil
    }

    val baseCanonicalFile = base.getCanonicalFile
    val dir = baseCanonicalFile / pathToShared
    val dirs: List[File] = getSub(dir / "src/main", "scala") ++ getSub(dir / "src/test", "scala")

    dirs.foldLeft(Seq[FileSettings]()) { (l, d) =>
      val linkPath = d.getParent.replace(dir.getPath,baseCanonicalFile.getPath )
      val linkName = d.getName.replace("scala", s"scala_${label}")
      val link     = file(s"$linkPath/$linkName")

      createSymbolicLink( d, link)
      l ++  Seq(cleanFiles += link)
    }
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
    skip in packageJSDependencies := false
  )


  // Cross version support
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

  def CrossVersionSources() : Seq[Setting[_]] =
    Seq(Compile, Test).map { sc =>
      unmanagedSourceDirectories in sc <++= (sourceDirectory in sc, scalaBinaryVersion) {
        (s, v) => Seq(s / ("scala_" + v) )
      }
    }

  def scalajsTargetSettings(name:String): Seq[Setting[_]] = {
    Seq(target := baseDirectory.value / "target" / name, cleanFiles += baseDirectory.value / "target", cleanFiles += baseDirectory.value / "target" / name)
  }

  def CrossVersionSharedSources(label:String) : Seq[Setting[_]] =
    Seq(Compile, Test).map { sc =>
      unmanagedSourceDirectories in sc <++= (sourceDirectory in sc, scalaBinaryVersion) {
        (s, v) => Seq(s / ("scala_" + v),  s / (s"scala_${label}_" + v),  s / s"scala_${label}")
      }
    }
}

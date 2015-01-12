package notests.build

import sbt._
import sbt.Keys._
import com.inthenow.sbt.scalajs._
import scala.scalajs.sbtplugin.ScalaJSPlugin._

object NotestsBuild extends Build {

  implicit val logger: Logger = ConsoleLogger()

  lazy val buildSettings: Seq[Setting[_]] = Seq(
    organization := "com.github.notests",
    scalaVersion := "2.11.4",
    crossScalaVersions := Seq("2.11.4", "2.10.4"),
    scalacOptions ++= Seq("-deprecation", "-unchecked")
  )

  /**
   * The root, aggregate project
   */
  lazy val rootModule = CrossRootModule(moduleName = "notests", defaultSettings = buildSettings)
  lazy val root       = rootModule.project(rootJvm, rootJs)
  lazy val rootJvm    = rootModule.jvmProject(rdfJvm, dbJvm, jena)
  lazy val rootJs     = rootModule.jsProject(rdfJs, dbJs)

  /**
   * The RDF Module
   *
   */
  lazy val rdfModule = CrossModule(
    id              = "rdf",
    baseDir         = "rdf",
    build           = SharedBuild,
    defaultSettings = buildSettings,
    modulePrefix    = "notests-"
  )

  lazy val rdf          = rdfModule.project(rdfJvm, rdfJs)
  lazy val rdfJvm       = rdfModule.jvmProject(rdfSharedJvm)
  lazy val rdfJs        = rdfModule.jsProject(rdfSharedJs)
  lazy val rdfSharedJvm = rdfModule.jvmShared()
  lazy val rdfSharedJs  = rdfModule.jsShared(rdfSharedJvm)

  /**
   * The Database Module
   */
  lazy val dbModule = CrossModule(
    id              = "db",
    baseDir         = "notestsDB",
    build           = SharedBuild,
    defaultSettings = buildSettings,
    modulePrefix    = "notests-")

  lazy val db          = dbModule.project(dbJvm, dbJs)
  lazy val dbJvm       = dbModule.jvmProject(dbSharedJvm)
  lazy val dbJs        = dbModule.jsProject(dbSharedJs)
  lazy val dbSharedJvm = dbModule.jvmShared().settings(libraryDependencies +=  "org.scalaz" %% "scalaz-core" % "7.0.6")
  lazy val dbSharedJs  = dbModule.jsShared(dbSharedJvm).settings(Seq(libraryDependencies += "com.github.japgolly.fork.scalaz" %%% "scalaz-core" % "7.0.6"):_*)//.asInstanceOf[Project]

  /**
   * The Jena module, just a plain old JS/JVM  project
   */
  lazy val jenaModule = Module(
    id              = "jena",
    baseDir         = "jena",
    build           = SingleBuild,
    target          = JvmTarget,
    defaultSettings = buildSettings,
    modulePrefix    = "notests-"
  )

  lazy val jena = jenaModule.jsProject
}

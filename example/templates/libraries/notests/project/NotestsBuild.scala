package notests.build

import sbt._
import sbt.Keys._
import com.inthenow.sbt.scalajs._
import SbtScalajs._
import scala.scalajs.sbtplugin.ScalaJSPlugin._

object NotestsBuild extends Build {

  /*
  * Update the Modules type to change how the project is built. To get git to ignore any local changes, just run
  *   git update-index --assume-unchanged project/NotestsBuild.scala
  *
  * To add changes to git:
  *
  *   git update-index --no-assume-unchanged project/NotestsBuild.scala
  *
  * Choose how to build Modules by un-commenting one of the three lines
  */
  type Modules =  Common
  //type Modules =  SymLinked
  //type Modules =  Shared

  implicit val logger: Logger = ConsoleLogger()

  lazy val buildSettings: Seq[Setting[_]] = Seq(
    organization := "com.github.notests",
    scalaVersion := "2.11.4",
    crossScalaVersions := Seq("2.11.4", "2.10.4"),
    scalacOptions ++= Seq("-deprecation", "-unchecked")
  )

  // A root, aggregate project
  lazy val rootModule = XRootModule(moduleName = "notests", defaultSettings = buildSettings)
  lazy val root       = rootModule.project(rootJvm, rootJs)
  lazy val rootJvm    = rootModule.jvmProject(rdfJvm, dbJvm, jena)
  lazy val rootJs     = rootModule.jsProject(rdfJs, dbJs)

  // The RDF Module
  type m = Modules#rdf
  lazy val rdfModule = XModule[m#Targets, m#BuildOps ](
    id = "rdf",
    baseDir = "rdf",
    defaultSettings = buildSettings,
    modulePrefix = "notests-")

  lazy val rdf          = rdfModule.project(rdfJvm, rdfJs)
  lazy val rdfJvm       = rdfModule.jvmProject(rdfSharedJvm)
  lazy val rdfJs        = rdfModule.jsProject(rdfSharedJs)
  lazy val rdfSharedJvm = rdfModule.jvmShared()
  lazy val rdfSharedJs  = rdfModule.jsShared(rdfSharedJvm)

  // The Database Module
  type db = Modules#db
  lazy val dbModule = XModule[db#Targets, db#BuildOps ](
    id = "db",
    baseDir = "notestsDB",
    defaultSettings = buildSettings,
    modulePrefix = "notests-")

  lazy val db          = dbModule.project(dbJvm, dbJs)
  lazy val dbJvm       = dbModule.jvmProject(dbSharedJvm)
  lazy val dbJs        = dbModule.jsProject(dbSharedJs)
  lazy val dbSharedJvm = dbModule.jvmShared().settings(libraryDependencies +=  "org.scalaz" %% "scalaz-core" % "7.0.6")
  lazy val dbSharedJs  = dbModule.jsShared(dbSharedJvm).settings(Seq(libraryDependencies += "com.github.japgolly.fork.scalaz" %%% "scalaz-core" % "7.0.6"):_*)

  // The Jena module, just a plain old JS/JVM  project
  type jena = Modules#jena
  lazy val jenaModule = SModule[jena#Targets, jena#BuildOps](id = "jena", baseDir = "jena", defaultSettings = buildSettings, modulePrefix = "notests-")

  lazy val jena = jenaModule.jvmProject[jena#Targets]
}

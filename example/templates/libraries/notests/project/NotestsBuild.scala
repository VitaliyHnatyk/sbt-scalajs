import sbt._
import sbt.Keys._
import com.inthenow.sbt.scalajs._

object NotestsBuild extends Build {
  import Dependencies._

  implicit val logger: Logger = ConsoleLogger()

  lazy val buildSettings: Seq[Setting[_]] = Seq(
    organization := "com.github.notests",
    scalaVersion := "2.11.5",
    crossScalaVersions := Seq("2.11.5", "2.10.4"),
    scalacOptions ++= Seq("-deprecation", "-unchecked")
  )

  /**
   * The root, aggregate project
   */
  lazy val rootM    = CrossModule(RootBuild,  id = "notests", defaultSettings = buildSettings)
  lazy val root     = rootM.project(Module, rootJvm, rootJs)
  lazy val rootJvm  = rootM.project(Jvm, rdfJvm, dbJvm, jena)
  lazy val rootJs   = rootM.project(Js, rdfJs, dbJs)

  /**
   * The RDF Module
   */
  lazy val rdfM = CrossModule(
    build           = SbtLinkedBuild,
    id              = "rdf",
    baseDir         = "rdf",
    defaultSettings = buildSettings,
    modulePrefix    = "notests-"
  )

  lazy val rdf          = rdfM.project(Module, rdfJvm, rdfJs)
  lazy val rdfJvm       = rdfM.project(Jvm, rdfSharedJvm)
  lazy val rdfJs        = rdfM.project(Js, rdfSharedJs)
  lazy val rdfSharedJvm = rdfM.project(JvmShared)
  lazy val rdfSharedJs  = rdfM.project(JsShared)

  /**
   * The Database Module
   */
  lazy val dbM      = CrossModule(
    build           = SbtLinkedBuild,
    id              = "db",
    baseDir         = "notestsDB",
    defaultSettings = buildSettings,
    modulePrefix    = "notests-")

  lazy val db          = dbM.project(Module, dbJvm, dbJs)
  lazy val dbJvm       = dbM.project(Jvm, dbSharedJvm)
  lazy val dbJs        = dbM.project(Js, dbSharedJs)
  lazy val dbSharedJvm = dbM.project(JvmShared).settings(libraryDependencies +=  parboiled2)
  lazy val dbSharedJs  = dbM.project(JsShared).settings(uTest:_*)

  /**
   * The Jena module, just a plain old JS/JVM  project
   */
  lazy val jenaModule = CrossModule(SingleBuild,
    id                = "jena",
    baseDir           = "jena",
    defaultSettings   = buildSettings,
    modulePrefix      = "notests-")

  lazy val jena = jenaModule.project(Jvm(id="SSS"))
}

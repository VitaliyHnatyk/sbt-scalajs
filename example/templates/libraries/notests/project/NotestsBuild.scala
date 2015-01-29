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
  lazy val rootJvm  = rootM.project(Jvm, rdfJvm, dbJvm, jena, sesameJvm)
  lazy val rootJs   = rootM.project(Js, rdfJs, dbJs)

  /**
   * The RDF Module
   */
  lazy val rdfM = CrossModule(
    buildType       = SbtLinkedBuild,
    id              = "rdf",
    baseDir         = "rdf",
    defaultSettings = buildSettings,
    modulePrefix    = "notests-"
  )

  lazy val rdf          = rdfM.project(Module, rdfJvm, rdfJs)
  lazy val rdfJvm       = rdfM.project(Jvm, rdfSharedJvm)
  lazy val rdfJs        = rdfM.project(Js, rdfSharedJs)
  lazy val rdfSharedJvm = rdfM.project(Jvm, Shared)
  lazy val rdfSharedJs  = rdfM.project(Js, Shared)

  /**
   * The Database Module
   */
  lazy val dbM      = CrossModule(
    buildType       = SharedBuild,
    id              = "db",
    baseDir         = "notestsDB",
    defaultSettings = buildSettings,
    modulePrefix    = "notests-")

  lazy val db          = dbM.project(Module, dbJvm, dbJs)
  lazy val dbJvm       = dbM.project(Jvm, dbSharedJvm)
  lazy val dbJs        = dbM.project(Js, dbSharedJs)
  lazy val dbSharedJvm = dbM.project(Jvm, Shared).settings(libraryDependencies +=  scalaz)
  lazy val dbSharedJs  = dbM.project(Js, Shared).settings(scalaz_js:_*)

  /**
   * The Jena module, just a plain old JS/JVM  project
   */
  lazy val jenaModule = CrossModule(SingleBuild,
    id                = "jena",
    baseDir           = "jena",
    defaultSettings   = buildSettings,
    modulePrefix      = "notests-")

  lazy val jena = jenaModule.project(Jvm(id="SSS"))

  /**
   * The Sesame module, just a plain old JVM  project, but in a shared project so we can specialise later
   */
  lazy val sesameM = CrossModule(SbtLinkedBuild,
    id                = "sesame",
    baseDir           = "sesame",
    defaultSettings   = buildSettings,
    modulePrefix      = "notests-",
    sharedLabel       = "common")

  lazy val sesame          = sesameM.project(Module, sesameJvm)
  lazy val sesameJvm       = sesameM.project(Jvm,Empty, sesameCommonJvm)
  lazy val sesameCommonJvm = sesameM.project(Jvm,Shared)

}

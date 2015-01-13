import sbt._
import sbt.Keys._
import com.inthenow.sbt.scalajs._
import scala.scalajs.sbtplugin.ScalaJSPlugin._

object CrossversionBuild extends Build {
  import Dependencies._

  implicit val logger: Logger = ConsoleLogger()

  lazy val buildSettings: Seq[Setting[_]] = Seq(
    organization := "com.github.CrossVersion",
    scalaVersion := "2.11.4",
    crossScalaVersions := Seq("2.11.4", "2.10.4"),
    scalacOptions ++= Seq("-deprecation", "-unchecked")
  )

  /**
   * The root, aggregate project
   */
  lazy val rootModule = CrossRootModule(moduleName = "CrossVersion", defaultSettings = buildSettings )
  lazy val root       = rootModule.project(rootJvm, rootJs)
  lazy val rootJvm    = rootModule.jvmProject(rdfJvm, dbJvm, jena)
  lazy val rootJs     = rootModule.jsProject(rdfJs, dbJs)

  /**
   * The RDF Module
   */
  lazy val rdfModule = CrossModule(
    id              = "rdf",
    baseDir         = "rdf",
    build           = SymLinkedBuild,
    defaultSettings = buildSettings,
    modulePrefix    = "crossversion-"
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
    baseDir         = "db",
    build           = SymLinkedBuild,
    defaultSettings = buildSettings ++ SbtScalajs.XScalaSettings,
    modulePrefix    = "crossversion-")

  lazy val db          = dbModule.project(dbJvm, dbJs)
  lazy val dbJvm       = dbModule.jvmProject(dbSharedJvm)
  lazy val dbJs        = dbModule.jsProject(dbSharedJs)
  lazy val dbSharedJvm = dbModule.jvmShared().settings(libraryDependencies +=  scalaz)
  lazy val dbSharedJs  = dbModule.jsShared(dbSharedJvm).settings(sclalajsQuery ++ scalaz_js:_*)

  /**
   * The Jena module, just a plain old JS/JVM  project
   */
  lazy val jenaModule = Module(
    id              = "jena",
    baseDir         = "jena",
    build           = SingleBuild,
    target          = JvmTarget,
    defaultSettings = buildSettings,
    modulePrefix    = "crossversion-"
  )

  lazy val jena = jenaModule.jsProject
}

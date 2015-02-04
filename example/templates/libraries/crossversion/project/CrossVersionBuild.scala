import sbt._
import sbt.Keys._
import com.inthenow.sbt.scalajs._
 
object CrossversionBuild extends Build {
  import Dependencies._

  implicit val logger: Logger = ConsoleLogger()

  lazy val buildSettings: Seq[Setting[_]] = Seq(
    organization := "com.github.CrossVersion",
    scalaVersion := "2.11.5",
    crossScalaVersions := Seq("2.11.5", "2.10.4"),
    scalacOptions ++= Seq("-deprecation", "-unchecked"),
    resolvers += Resolver.url("inthenow-releases", url("http://dl.bintray.com/inthenow/releases"))(Resolver.ivyStylePatterns)
  )

  /**
   * The root, aggregate project
   */
  lazy val rootModule = CrossModule(RootBuild,  id = "crossversion", defaultSettings = buildSettings)
  lazy val root       = rootModule.project(Module, rootJvm, rootJs)
  lazy val rootJvm    = rootModule.project(Jvm, rdfJvm, dbJvm, jena)
  lazy val rootJs     = rootModule.project(Js, rdfJs, dbJs)

  /**
   * The RDF Module
   */
  lazy val rdfModule = CrossModule(
    id              = "rdf",
    baseDir         = "rdf",
    buildType       = SymLinkedBuild,
    defaultSettings = buildSettings,
    modulePrefix    = "crossversion-"
  )

  lazy val rdf          = rdfModule.project(Module, rdfJvm, rdfJs)
  lazy val rdfJvm       = rdfModule.project(Jvm, rdfSharedJvm)
  lazy val rdfJs        = rdfModule.project(Js, rdfSharedJs)
  lazy val rdfSharedJvm = rdfModule.project(Jvm, Shared)
  lazy val rdfSharedJs  = rdfModule.project(Js, Shared)

  /**
   * The Database Module
   */
  lazy val dbModule = CrossModule(
    id              = "db",
    baseDir         = "db",
    buildType       = SbtLinkedBuild,
    defaultSettings = buildSettings ++ SbtScalajs.XScalaMacroDependencies,
    modulePrefix    = "crossversion-")

  lazy val db          = dbModule.project(Module, dbJvm, dbJs)
  lazy val dbJvm       = dbModule.project(Jvm, dbSharedJvm)
  lazy val dbJs        = dbModule.project(Js, dbSharedJs)
  lazy val dbSharedJvm = dbModule.project(Jvm, Shared).settings(libraryDependencies +=  scalaz)
  lazy val dbSharedJs  = dbModule.project(Js, Shared, dbSharedJvm).settings(sclalajsDom:_*)

  /**
   * The Jena module, just a plain old JS/JVM  project
   */
  lazy val jenaModule = CrossModule(SingleBuild,
    id              = "jena",
    baseDir         = "jena",

    defaultSettings = buildSettings,
    modulePrefix    = "crossversion-")

  lazy val jena = jenaModule.project(Jvm(id="SSS"))
   
}

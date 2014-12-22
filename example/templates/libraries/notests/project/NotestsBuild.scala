
import sbt._
import sbt.Keys._
import com.inthenow.sbt.scalajs._

object NotestsBuild extends Build {

  implicit val logger: Logger = ConsoleLogger()

  lazy val buildSettings: Seq[Setting[_]] = Seq(
    organization := "com.github.???",
    scalaVersion := "2.11.4",
    crossScalaVersions := Seq("2.11.4", "2.10.4"),
    scalacOptions ++= Seq("-deprecation", "-unchecked")
  )

  // Todo: rather than the import, I would rather have something like:
  // val module = XModule[LinkedProject](id = "notests", defaultSettings = buildSettings, modulePrefix = "banana-")

  import LinkedProject._

  val module = XModule(id = "notests", defaultSettings = buildSettings, modulePrefix = "banana-")

  lazy val rdf = module.project(prjJvm, prjJs)
  lazy val prjJvm = module.jvmProject()
  lazy val prjJs = module.jsProject()

  /*
    // This is an alternative with actual common libraries
    import SharedProject._
    val module = XModule(id = "notests", defaultSettings = buildSettings, modulePrefix = "banana-")

    lazy val rdf2 = module.project(prjJvm, prjJs)
    lazy val prjJvm    = module.jvmProject(sharedjvm)
    lazy val prjJs     = module.jsProject(sharedjs)
    lazy val sharedjvm = module.jvmShared()
    lazy val sharedjs  = module.jsShared(sharedjvm)
  */

}

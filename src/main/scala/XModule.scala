package com.inthenow.sbt.scalajs


import sbt._
import sbt.Keys._

case class XModule (id: String,
                   baseDir: String = ".",
                   modulePrefix:String = "",
                   sharedLabel:String = "shared",
                   defaultSettings: Seq[Def.Setting[_]] = Seq())
                  (implicit val jvmTarget: JvmTarget, implicit val jsTarget: JsTarget, implicit val log:Logger ) {

  lazy val base = file(baseDir)

  def getModuleName() = {
    s"${id}_module"
  }

  def getProjectBase(projectId: String, projectDir: String, hidden:Boolean = false):File = {
    if (hidden) base / s".$projectDir" else base / projectDir
  }

  def getProjectId(projectId: String, projectDir: String) = {
    s"${id}_$projectDir"
  }

  def getProjectName(projectId: String, projectDir: String) = {
    s"${modulePrefix}${id}"
  }

  def getDefaultSettings: Seq[Def.Setting[_]] = defaultSettings

  def project(jvm: Project, js: Project): Project =
    Project(
      id = id,
      base = base,
      settings = SbtScalajs.noRootSettings ++ getDefaultSettings ++ Seq( name := { getModuleName() } )
    ).dependsOn(jvm, js).aggregate(jvm, js)
  //implicit ops: ProjectOps = LinkedProject(this)
}



class ProjectOps

object SharedProject {

  // use these ops to create a module with shared code compiled to separate artifacts
  implicit class SharedProjectOps(m: XModule) extends ProjectOps {
    def xShared(tp: XTarget, hidden: Boolean = false): Project =
      Project(
        id = getSharedProjectId(m.id, tp.name),
        base = getSharedProjectBase(m.id, tp.name, hidden),
        settings = m.getDefaultSettings ++ tp.settings ++ Seq(name := {
          getSharedProjectName(m.id, tp.name)
        })
      )

    def jvmShared(): Project = xShared(m.jvmTarget)

    def jsShared(shared: Project): Project = xShared(m.jsTarget, true).enablePlugins(SbtScalajs).settings(SbtScalajs.linkedSources(shared): _*)


    def getSharedProjectBase(projectId: String, projectDir: String, hidden: Boolean = false): File = {
      if (hidden) m.base / s".${m.sharedLabel}_$projectDir" else m.base / m.sharedLabel
    }

    def getSharedProjectId(projectId: String, projectDir: String) = {
      s"${m.id}_${m.sharedLabel}_$projectDir"
    }

    def getSharedProjectName(projectId: String, projectDir: String) = {
      s"${m.modulePrefix}${m.id}_${m.sharedLabel}"
    }

    def jvmProject(depends: Project) = xProject(depends, m.jvmTarget)

    def jsProject(depends: Project) = xProject(depends, m.jsTarget).enablePlugins(SbtScalajs)

    def xProject(depends: Project, tp: XTarget): Project =
      Project(
        id = m.getProjectId(m.id, tp.name),
        base = m.getProjectBase(m.id, tp.name),
        settings = m.getDefaultSettings ++ tp.settings ++ Seq(name := {
          m.getProjectName(m.id, tp.name)
        })
      ).dependsOn(depends % "compile;test->test").aggregate(depends)
  }

}
object LinkedProject {

 def apply(m: XModule) = new LinkedProjectOps(m)

  // use these ops to create a module with real shared code using symbolic links
  implicit class LinkedProjectOps(m: XModule)  extends ProjectOps {

    def jvmProject() = xProject(m.jvmTarget)

    def jsProject() = xProject(m.jsTarget).enablePlugins(SbtScalajs)

    def xProject(tp: XTarget): Project = {
      // if we can find a shared directory, link to it
      SbtScalajs.linkToShared(m.getProjectBase(m.id, tp.name), s"../${m.sharedLabel}")(m.log)

      // Create the project
      Project(
        id = m.getProjectId(m.id, tp.name),
        base = m.getProjectBase(m.id, tp.name),
        settings = m.getDefaultSettings ++ tp.settings ++ Seq(name := {
          m.getProjectName(m.id, tp.name)
        })
      )
    }
  }
}

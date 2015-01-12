package com.inthenow.sbt.scalajs

import sbt._
import sbt.Keys._

sealed trait CrossBuildOps {

  def getSharedProjectName(projectId: String, projectDir: String): String

  def getSharedProjectId(projectId: String, projectDir: String): String

  def getSharedProjectBase(projectId: String, projectDir: String, hidden: Boolean = false): File

  def jvmProject(p: Project): Project

  def jsProject(p: Project): Project

  def jvmShared(): Project

  def jsShared(shared: Project): Project

}

object CrossBuildOps {
  def setBuildType(module: String, build: CrossBuildType): String = {
    val result = build match {
      case SharedBuild     => "SharedBuild"
      case SymLinkedBuild  => "SymLinkedBuild"
      case CommonBaseBuild => "CommonBaseBuild"
      case _ => throw new Error(s"Unknown build type ${build.toString} for ${module}.build")
    }
    System.setProperty(s"${module}.build", result)
    result
  }

  def getBuildType(module: String, default: CrossBuildType): CrossBuildType = {
    val result = Option(System.getProperty(s"${module}.build")) match {
      case Some("SharedBuild")     => SharedBuild
      case Some("SymLinkedBuild")  => SymLinkedBuild
      case Some("CommonBaseBuild") => CommonBaseBuild
      case None => default
      case opt: Option[String] => throw new Error(s"Unknown build type $opt for ${module}.build")
      case _ => default
    }
    result
  }
}

sealed trait CrossBuildType {
  def ops(m: CrossModule):CrossBuildOps
}

case class SymLinkedBuild (m: CrossModule  ) extends CrossBuildOps {

  protected def xShared(tp: Target, hidden: Boolean = true): Project =
    Project(
      id = getSharedProjectId(m.id, tp.name),
      base = getSharedProjectBase(m.id, tp.name, hidden),
      settings = SbtScalajs.noRootSettings ++ m.getDefaultSettings ++ tp.settings ++ Seq(name := {
        getSharedProjectName(m.id, tp.name)
      }
      ))

  def jvmShared():  Project = xShared(m.targets.jvm)

  def jsShared(shared:  Project): Project =  xShared(m.targets.js)

  def getSharedProjectBase(projectId: String, projectDir: String, hidden: Boolean = false): File = {
    if (hidden) m.base / s".${m.sharedLabel}_$projectDir" else m.base / m.sharedLabel
  }

  def getSharedProjectId(projectId: String, projectDir: String) = {
    s"${m.id}_${m.sharedLabel}_$projectDir"
  }

  def getSharedProjectName(projectId: String, projectDir: String) = {
    s"${m.modulePrefix}${m.id}_${m.sharedLabel}"
  }
  def jvmProject(p: Project) = xProject(p, m.targets.jvm)

  def jsProject(p: Project) = xProject(p, m.targets.js).enablePlugins(SbtScalajs)

  private def xProject(p: Project, tp: Target): Project = {
    // if we can find a shared directory, link to it
    SbtScalajs.linkToShared(m.getProjectBase(m.id, tp.name), s"../${m.sharedLabel}")(m.log)
    val setings:Seq[sbt.Def.Setting[_]] = p.settings
    val aggregates:Seq[sbt.ProjectReference] = p.aggregate

    // Create the project
    Project(
      id = m.getProjectId(m.id, tp.name),
      base = m.getProjectBase(m.id, tp.name),
      settings = m.getDefaultSettings ++ tp.settings ++ Seq(name := {
        m.getProjectName(m.id, tp.name)
      })
    ).settings(setings:_*)
      .aggregate(aggregates:_*)
      .dependsOn(p.dependencies: _*)
      .enablePlugins(p.plugins)
      //.disablePlugins(p.disablePlugins: _*)
  }
}

case object SymLinkedBuild extends CrossBuildType {
  def ops(m: CrossModule) = new SymLinkedBuild (m)
}

case class SharedBuild (m: CrossModule )
    extends SharedBuildOpsBase[SharedBuild](m)

case object SharedBuild extends CrossBuildType {
  def ops(m: CrossModule) = new SharedBuild (m)
}

case class CommonBaseBuildOps (m: CrossModule  )
  extends SharedBuildOpsBase[CommonBaseBuildOps] (m) {

  override def jsShared(shared: Project): Project = xShared(m.targets.js, true).enablePlugins(SbtScalajs)

  override def getSharedProjectBase(projectId: String, projectDir: String, hidden: Boolean = false): File = {
    m.base / m.sharedLabel
  }
}

case object CommonBaseBuild extends CrossBuildType {
  def ops(m: CrossModule) = new CommonBaseBuildOps (m)
}

abstract class SharedBuildOpsBase[B <: CrossBuildOps](m: CrossModule ) extends CrossBuildOps  {

  protected def xShared(tp: Target, hidden: Boolean = false): Project =
    Project(
      id = getSharedProjectId(m.id, tp.name),
      base = getSharedProjectBase(m.id, tp.name, hidden),
      settings = m.getDefaultSettings ++ tp.settings ++ Seq(name := {
        getSharedProjectName(m.id, tp.name)
      }
    ))

  def jvmShared():  Project = xShared(m.targets.jvm)

  def jsShared(shared:  Project): Project = {
    xShared(m.targets.js, true).enablePlugins(SbtScalajs).settings(SbtScalajs.linkedSources(shared): _*)
  }

  def getSharedProjectBase(projectId: String, projectDir: String, hidden: Boolean = false): File = {
    if (hidden) m.base / s".${m.sharedLabel}_$projectDir" else m.base / m.sharedLabel
  }

  def getSharedProjectId(projectId: String, projectDir: String) = {
    s"${m.id}_${m.sharedLabel}_$projectDir"
  }

  def getSharedProjectName(projectId: String, projectDir: String) = {
    s"${m.modulePrefix}${m.id}_${m.sharedLabel}"
  }

  def jvmProject(depends: Project) = xProject(depends, m.targets.jvm)

  def jsProject(depends: Project) = xProject(depends, m.targets.js).enablePlugins(SbtScalajs)

  protected def xProject(depends: Project, tp: Target): Project = {
    val aggregates: Seq[sbt.ProjectReference] = depends.aggregate

    Project(
      id = m.getProjectId(m.id, tp.name),
      base = m.getProjectBase(m.id, tp.name),
      settings = m.getDefaultSettings ++ tp.settings ++ Seq(name := {
        m.getProjectName(m.id, tp.name)
      })
    ).dependsOn(depends % "compile;test->test").aggregate(aggregates: _*)
  }
}

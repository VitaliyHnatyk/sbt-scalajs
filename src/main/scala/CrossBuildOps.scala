package com.inthenow.sbt.scalajs

import sbt._
import sbt.Keys._
import SbtScalajs._

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

  def getKey(module: CrossModule) = s"${module.modulePrefix}${module.id}.build"

  def setBuildType(module: CrossModule, build: CrossBuildType): String = {
    val key = getKey(module)
    val result = build match {
      case SharedBuild     => "SharedBuild"
      case SymLinkedBuild  => "SymLinkedBuild"
      case SbtLinkedBuild  => "SbtLinkedBuild"
      case CommonBaseBuild => "CommonBaseBuild"
      case _ => throw new Error(s"Unknown build type ${build.toString} for $key")
    }
    System.setProperty(key, result)
    result
  }

  def getBuildType(module: CrossModule): CrossBuildType = { //, default: CrossBuildType): CrossBuildType = {
    val key = getKey(module)
    val result = Option(System.getProperty(key)) match {
      case Some("SharedBuild")     => SharedBuild
      case Some("SymLinkedBuild")  => SymLinkedBuild
      case Some("SbtLinkedBuild")  => SbtLinkedBuild
      case Some("CommonBaseBuild") => CommonBaseBuild
      case None => module.build
      case opt: Option[String] => throw new Error(s"Unknown build type $opt for $key")
      case _ => module.build
    }
    result
  }

}

sealed trait CrossBuildType {
  def ops(m: CrossModule):CrossBuildOps
}

case object SymLinkedBuild extends CrossBuildType {
  def ops(m: CrossModule) = new SymLinkedBuild (m)
}

case class SymLinkedBuild (m: CrossModule  ) extends LinkedBuild(m) {
  protected def xProject(p: Project, tp: Target): Project = {
    val links = linkToShared(m.getProjectBase(m.id, tp.name), s"../${m.sharedLabel}",m.sharedLabel )(m.log)
    xProject(p, tp, links)
  }
}

case object SbtLinkedBuild extends CrossBuildType {
  def ops(m: CrossModule) = new SbtLinkedBuild (m)
}

case class SbtLinkedBuild (m: CrossModule) extends LinkedBuild(m) {

  protected def xProject(p: Project, tp: Target): Project = {
    // if we can find a shared directory, link to it and create the project
    val links = linkedSources(p)
    xProject(p, tp, links)
  }
}

abstract class LinkedBuild (m: CrossModule) extends CrossBuildOps {

  protected def xShared(tp: Target, hidden: Boolean = true): Project =
    Project(
      id = getSharedProjectId(m.id, tp.name),
      base = getSharedProjectBase(m.id, tp.name, hidden),
      settings = SbtScalajs.noRootSettings ++ m.getDefaultSettings ++ tp.settings ++
        scalajsTargetSettings(tp.id) ++ CrossVersionSources(m.sharedLabel)
    )

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

  protected def xProject(p: Project, tp: Target, links: Seq[FileSettings]): Project = {
    val settings:Seq[sbt.Def.Setting[_]] = p.settings
    val aggregates:Seq[sbt.ProjectReference] = p.aggregate

    // Create the project
    Project(
      id = m.getProjectId(m.id, tp.name),
      base = m.getProjectBase(m.id, tp.name),
      settings = m.getDefaultSettings ++ tp.settings ++ links  ++
        scalajsTargetSettings(tp.id) ++ CrossVersionSharedSources(m.sharedLabel) ++ Seq(
        name := {
          m.getProjectName(m.id, tp.name)
        })
    ).settings(settings:_*)
      .aggregate(aggregates:_*)
      .dependsOn(p.dependencies: _*)
      .enablePlugins(p.plugins)
    //.disablePlugins(p.disablePlugins: _*)
  }

  protected def xProject(p: Project, tp: Target): Project
}


case class SharedBuild (m: CrossModule) extends SharedBuildOpsBase (m)

case object SharedBuild extends CrossBuildType {
  def ops(m: CrossModule) = new SharedBuild (m)
}

case class CommonBaseBuildOps (m: CrossModule) extends SharedBuildOpsBase  (m) {

  override def jsShared(shared: Project): Project = xShared(m.targets.js, true).enablePlugins(SbtScalajs)

  override def getSharedProjectBase(projectId: String, projectDir: String, hidden: Boolean = false): File = {
    m.base / m.sharedLabel
  }
}

case object CommonBaseBuild extends CrossBuildType {
  def ops(m: CrossModule) = new CommonBaseBuildOps (m)
}

abstract class SharedBuildOpsBase(m: CrossModule ) extends CrossBuildOps  {

  protected def xShared(tp: Target, hidden: Boolean = false): Project =
    Project(
      id = getSharedProjectId(m.id, tp.name),
      base = getSharedProjectBase(m.id, tp.name, hidden),
      settings = m.getDefaultSettings ++ tp.settings  ++
        scalajsTargetSettings(tp.id) ++ CrossVersionSources(m.sharedLabel) ++ Seq(name := {
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

  def getSharedProjectId(projectId: String, projectDir: String) = s"${m.id}_${m.sharedLabel}_$projectDir"

  def getSharedProjectName(projectId: String, projectDir: String) = s"${m.modulePrefix}${m.id}_${m.sharedLabel}"

  def jvmProject(depends: Project) = xProject(depends, m.targets.jvm)

  def jsProject(depends: Project) = xProject(depends, m.targets.js).enablePlugins(SbtScalajs)

  protected def xProject(depends: Project, tp: Target): Project = {

    Project(
      id = m.getProjectId(m.id, tp.name),
      base = m.getProjectBase(m.id, tp.name),
      settings = m.getDefaultSettings ++ tp.settings  ++
        scalajsTargetSettings(tp.id) ++ CrossVersionSources(m.sharedLabel) ++ Seq(name := {
        m.getProjectName(m.id, tp.name)
      })
    ).dependsOn(depends % "compile;test->test").aggregate(depends)
  }
}

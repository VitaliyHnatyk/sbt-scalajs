package com.inthenow.sbt.scalajs


import sbt._
import sbt.Keys._

abstract class XSharedBuildOpsBase[T <: XTargetsSig, B <: BuildOps[T]](m: XModule[T, B]) extends BuildOps[T] {
  type SharedProject = Project

  protected def xShared(tp: XTarget, hidden: Boolean = false): Project =
    Project(
      id = getSharedProjectId(m.id, tp.name),
      base = getSharedProjectBase(m.id, tp.name, hidden),
      settings = m.getDefaultSettings ++ tp.settings ++ Seq(name := {
        getSharedProjectName(m.id, tp.name)
      })
    )

  def jvmShared(): Project = xShared(m.targets.jvm)

  def jsShared(shared: Project): Project = {
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

  protected def xProject(depends: Project, tp: XTarget): Project =
    Project(
      id = m.getProjectId(m.id, tp.name),
      base = m.getProjectBase(m.id, tp.name),
      settings = m.getDefaultSettings ++ tp.settings ++ Seq(name := {
        m.getProjectName(m.id, tp.name)
      })
    ).dependsOn(depends % "compile;test->test").aggregate(depends)


}

package com.inthenow.sbt.scalajs

import sbt._

class RootBuild(m: ModuleOps) extends CrossBuildOps(m, "RootBuild") {

  def project(t: Target, projects: Project*): Project = t.mkProject(this, projects: _*)

  // For the root Module, the shared projects are in fact just aggregate projects
  override def getSharedProjectBase(projectId: String, projectDir: String, hidden: Boolean): File = {
     m.base / s".${projectId}_$projectDir"
  }

  override def getSharedProjectId(projectId: String, projectDir: String) = s"${projectId}_$projectDir"

  override def getSharedProjectName(projectId: String, projectDir: String) = s"${m.module.modulePrefix}${projectId}"
}

case object RootBuild extends BuildType {
  def apply(m: ModuleOps) = new RootBuild(m)
}

case class RootBuildProjectOps(b: CrossBuildOps) extends SharedBuildProjectOpsBase(b) {
  override  def mkProject(tp: Target, projects: Project*): Project = tp.mkProject(mkSharedProject(tp, true, Seq()).addProjects(projects:_*  ))
}
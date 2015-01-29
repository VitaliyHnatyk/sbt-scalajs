package com.inthenow.sbt.scalajs


import sbt._
import sbt.Keys._

class RootBuild(m: CrossModuleOps)( implicit log: Logger) extends SharedBuildOps(m, "RootBuild") {

  def mkProject(target:TargetOps , projects:Seq[Project]): Project =  {
    val p= target.projectOps
    val params = p.targetProjectParams(target, true, Seq(), projects)
    val options = p.targetProjectOptions.copy(hidden = true, addProjects = true)

    target.mkProject(this, params, options)
  }

  // For the root Module, the shared projects are in fact just aggregate projects
  override def getProjectBase(projectId: String, projectDir: String, hidden: Boolean): File = {
    m.getBase / s".${projectId}_$projectDir"
  }

  override def getProjectId(projectId: String, projectDir: String) = s"${projectId}_$projectDir"

  override def getProjectName(projectId: String, projectDir: String) = s"${m.crossModule.modulePrefix}${projectId}"
}

case object RootBuild extends SharedBuildType {
  def getBuildOps(m: CrossModuleOps, projectType:Empty)( implicit log: Logger): BuildOps = new RootBuild(m)
  def getBuildOps(m: CrossModuleOps,projectType:Standard )( implicit log: Logger) = new RootBuild(m)
  def getBuildOps(m: CrossModuleOps,projectType:Shared)( implicit log: Logger):BuildOps =  new RootBuild(m)
}

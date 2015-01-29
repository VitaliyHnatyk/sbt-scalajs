package com.inthenow.sbt.scalajs

import sbt._
import sbt.Keys._
import SbtScalajs._

class SbtLinkedBuild(m: CrossModuleOps)(implicit log: Logger) extends BuildOps(m, "SbtLinkedBuild") {

  def mkProject(target: TargetOps, projects: Seq[Project]): Project = {
    require(projects.length > 0, s"Error - SbtLinkedBuild: No project to link to for target ${target.id} in project ${target.projectOps.crossModule.getModuleName()}")

    val p = target.projectOps
    val options = p.targetProjectOptions.copy(copyProject = true, addProjects = false)

    val settings: Seq[Setting[_]] = projects.map(d => aggregate in d.project := false)
    val p1 = p.targetProjectParams(target, options.hidden, p.projectNameSettings(target) ++ settings, projects)

    val params = p1.copy(settings = p1.settings ++ addRelDirectories(p1.base, s"../${p.crossModule.sharedLabel}"))
    target.mkProject(this, params, options)

  }
}

case object SbtLinkedBuild extends BuildType {
  def getBuildOps(m: CrossModuleOps, projectType: Empty)(implicit log: Logger): BuildOps = new EmptyOps(m)

  def getBuildOps(m: CrossModuleOps, projectType: Standard)(implicit log: Logger) = new SbtLinkedBuild(m)

  def getBuildOps(m: CrossModuleOps, projectType: Shared)(implicit log: Logger) = new SharedOps(m)

  class SharedOps(m: CrossModuleOps)(implicit log: Logger) extends LinkedBuildBase.SharedOps(m, "SbtLinkedBuild") {
  }

  class EmptyOps(m: CrossModuleOps)(implicit log: Logger) extends EmptyBuildOps(m, "SbtLinkedBuild") {
    def mkProject(target: TargetOps, projects: Seq[Project]): Project = {
      require(projects.length > 0, s"Error - SbtLinkedBuild: No project to link to for target ${target.id} in project ${target.projectOps.crossModule.getModuleName()}")
      val pr = projects.head

      val p = target.projectOps
      val options = p.targetProjectOptions.copy(copyProject = true, addProjects = false)

      val settings: Seq[Setting[_]] = projects.map(d => aggregate in d.project := false)

      val p1 = p.targetProjectParams(target, options.hidden, p.projectNameSettings(target) ++ settings, projects)

      val params = p1.copy(settings = p1.settings ++ addRelDirectories(p1.base, s"../${p.crossModule.sharedLabel}"))

      target.mkProject(this, params, options)

    }
  }

}

 

package com.inthenow.sbt.scalajs

import sbt.Keys._
import sbt._
import SbtScalajs._

class SymLinkedBuild(m: CrossModuleOps)(implicit log: Logger) extends BuildOps(m, "SymLinkedBuild") {

  def mkProject(target: TargetOps, projects: Seq[Project]): Project = {
    require(projects.length > 0, s"Error - SbtLinkedBuild: No project to link to for target ${target.id} in project ${target.projectOps.crossModule.getModuleName()}")
    val p = target.projectOps
    val options = p.targetProjectOptions.copy(copyProject = true, addProjects = false, callBuildInit = true)

    val settings: Seq[Setting[_]] = projects.map(d => aggregate in d.project := false)

    val p1 = p.targetProjectParams(target, options.hidden, p.projectNameSettings(target) ++ settings ++ CrossVersionSharedSources(p.moduleOps.getSharedLabel), projects)

    val init = () => {
      val mOps = target.projectOps.moduleOps
      linkToShared(p1.base, s"../${mOps.getSharedLabel}", mOps.getSharedLabel)(mOps.crossModule.log)
    }

    val params = p1.copy(buildInit = init)
    target.mkProject(this, params, options)
  }
}

case object SymLinkedBuild extends SharedBuildType {
  def getBuildOps(m: CrossModuleOps, projectType: Empty)(implicit log: Logger): BuildOps = new EmptyOps(m)

  def getBuildOps(m: CrossModuleOps, projectType: Standard)(implicit log: Logger) = new SymLinkedBuild(m)

  def getBuildOps(m: CrossModuleOps, projectType: Shared)(implicit log: Logger) = new SharedOps(m)

  class SharedOps(m: CrossModuleOps)(implicit log: Logger) extends LinkedBuildBase.SharedOps(m, "SymLinkedBuild") {
  }

  class EmptyOps(m: CrossModuleOps)(implicit log: Logger) extends EmptyBuildOps(m, "SbtLinkedBuild") {
    def mkProject(target: TargetOps, projects: Seq[Project]): Project = {

      require(projects.length > 0, s"Error - SbtLinkedBuild: No project to link to for target ${target.id} in project ${target.projectOps.crossModule.getModuleName()}")
      val pr = projects.head

      val p = target.projectOps
      val options = p.targetProjectOptions.copy(copyProject = true, addProjects = false, callBuildInit = true)

      val settings: Seq[Setting[_]] = projects.map(d => aggregate in d.project := false)

      // val params = p.targetProjectParams(target, options.hidden, p.projectNameSettings(target) ++  settings ++ linkedSources(pr), projects)
      val p1 = p.targetProjectParams(target, options.hidden, p.projectNameSettings(target) ++ settings ++ CrossVersionSharedSources(p.moduleOps.getSharedLabel), projects)

      val init = () => {
        val mOps = target.projectOps.moduleOps
        linkToShared(p1.base, s"../${mOps.getSharedLabel}", mOps.getSharedLabel)(mOps.crossModule.log)
      }

      val params = p1.copy(buildInit = init)
      target.mkProject(this, params, options)
    }
  }

}

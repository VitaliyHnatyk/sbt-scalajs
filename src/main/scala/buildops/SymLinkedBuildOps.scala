package com.inthenow.sbt.scalajs

import sbt._
import SbtScalajs._

class SymLinkedBuild(m: CrossModuleOps)( implicit log: Logger) extends LinkedBuildBase.Std(m, "SymLinkedBuild") {

  def mkProject(t: TargetOps, projects: Seq[Project]): Project = {
    val init = () => {
      val mOps = t.projectOps.moduleOps
      linkToShared(getProjectBase(mOps.crossModule.id, t.name), s"../${mOps.getSharedLabel}", mOps.getSharedLabel)(mOps.crossModule.log)
    }

    mkLinkedProject(t, init, projects)
  }
}

case object SymLinkedBuild extends SharedBuildType{
  def getBuildOps(m: CrossModuleOps, projectType:Empty)( implicit log: Logger): BuildOps = new EmptyOps(m)
  def getBuildOps(m: CrossModuleOps, projectType: Standard)( implicit log: Logger) = new SymLinkedBuild(m)
  def getBuildOps(m: CrossModuleOps, projectType: Shared)( implicit log: Logger) = new SharedOps(m)

  class SharedOps(m: CrossModuleOps)( implicit log: Logger) extends LinkedBuildBase.SharedOps(m, "SymLinkedBuild") {

  }

  class EmptyOps(m: CrossModuleOps)( implicit log: Logger) extends LinkedBuildBase.EmptyOps(m, "SbtLinkedBuild") {
    def mkProject(t: TargetOps, projects: Seq[Project]): Project = {
      val init = () => {
        val mOps = t.projectOps.moduleOps
        linkToShared(getProjectBase(mOps.crossModule.id, t.name), s"../${mOps.getSharedLabel}", mOps.getSharedLabel)(mOps.crossModule.log)
      }

      mkLinkedProject(t, init, projects)
    }
  }
}

package com.inthenow.sbt.scalajs

import sbt._
import SbtScalajs._

class SymLinkedBuild(m: CrossModuleOps) extends LinkedBuildBase.Std(m, "SymLinkedBuild") {

  def mkProject(t: TargetOps, projects: Seq[Project]): Project = {
    val p = projects.head
    def init():Seq[Setting[_]] = {
      val mOps = t.projectOps.moduleOps
      linkToShared(getProjectBase(mOps.crossModule.id, t.name), s"../${mOps.getSharedLabel}", mOps.getSharedLabel)(mOps.crossModule.log)
    }
    mkLinkedProject(t, init, p)
  }
}

case object SymLinkedBuild extends SharedBuildType{
  def getBuildOps(m: CrossModuleOps, projectType: Standard) = new SymLinkedBuild(m)
  def getBuildOps(m: CrossModuleOps, projectType: Shared) = new SharedOps(m)

  class SharedOps(m: CrossModuleOps) extends LinkedBuildBase.SharedOps(m, "SymLinkedBuild") {

  }
}

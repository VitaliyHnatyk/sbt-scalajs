package com.inthenow.sbt.scalajs

import sbt._
import SbtScalajs._

class SymLinkedBuild(m: ModuleOps) extends LinkedBuild(m, "SymLinkedBuild") {
  def project(t: Target, projects: Project*): Project = t.mkProject(this, projects:_*)
}

case object SymLinkedBuild extends BuildType {
  def apply(m: ModuleOps) = new SymLinkedBuild(m)
}


case class  SymLinkedBuildProjectOps(b: CrossBuildOps) extends LinkedBuildProjectOps(b)  {
  def mkProject(tp: Target, projects:  Project* ): Project = {
    val p = projects.head
    val mOps =b.moduleOps
    val links = linkToShared(mOps.getProjectBase(mOps.module.id, tp.name), s"../${mOps.sharedLabel}",mOps.sharedLabel )(mOps.module.log)
    mkLinkedProject(tp, links, p)
  }

}

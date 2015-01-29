package com.inthenow.sbt.scalajs

import sbt._

class SingleBuild(m: CrossModuleOps, projectType:ProjectType)( implicit log: Logger) extends BuildOps(m, "SingleBuild") {

  def mkProject(target:TargetOps , projects:Seq[Project]): Project = {
    val p= target.projectOps
    val params = p.targetProjectParams(target, false, p.projectNameSettings(target), projects)
    val options = p.targetProjectOptions

    target.mkProject(this, params, options)
  }
}

case object SingleBuild extends BuildType {
  def getBuildOps(m: CrossModuleOps, projectType:Empty)( implicit log: Logger): BuildOps = new SingleBuild(m, projectType)
  def getBuildOps(m: CrossModuleOps, projectType:Standard)( implicit log: Logger) = new SingleBuild(m, projectType)
  def getBuildOps(m: CrossModuleOps,projectType:Shared)( implicit log: Logger): BuildOps = new SingleBuild(m, projectType)
}



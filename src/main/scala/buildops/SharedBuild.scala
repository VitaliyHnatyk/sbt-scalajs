package com.inthenow.sbt.scalajs

import sbt._
import SbtScalajs._
class SharedBuild(m: CrossModuleOps)( implicit log: Logger) extends SharedBuildBase.Std(m, "SharedBuild") {
}

case object SharedBuild extends BuildType {
  def getBuildOps(m: CrossModuleOps,projectType:Empty)( implicit log: Logger) = new EmptyOps(m)
  def getBuildOps(m: CrossModuleOps,projectType:Standard)( implicit log: Logger) = new SharedBuild(m)
  def getBuildOps(m: CrossModuleOps,projectType:Shared)( implicit log: Logger) = new SharedOps(m)

  class SharedOps (m: CrossModuleOps)( implicit log: Logger) extends SharedBuildOps(m, "SharedBuild") {
    def mkProject(target: TargetOps, projects: Seq[Project]): Project = {
      val p = target.projectOps
      val options = p.targetProjectOptions.copy(hidden = true)
      val p1 = p.targetProjectParams(target, options.hidden, p.projectNameSettings(target) , projects)
      val params = p1.copy(settings = p1.settings  ++ addRelDirectories(p1.base, s"../${p.crossModule.sharedLabel}") )


      target.mkProject(this, params, options)
    }
  }
  class EmptyOps(m: CrossModuleOps)(implicit log: Logger) extends SharedBuildBase.EmptyOps(m, "CommonBaseBuild")
}


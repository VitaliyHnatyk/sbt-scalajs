package com.inthenow.sbt.scalajs


import sbt._
import sbt.Keys._
import SbtScalajs._

object LinkedBuildBase {

  abstract class SharedOps(m: CrossModuleOps, buildName: String)( implicit log: Logger) extends SharedBuildOps(m, buildName) {
    def mkProject(target: TargetOps, projects: Seq[Project]): Project = {
      val p = target.projectOps
      val options = p.targetProjectOptions.copy(hidden= true, callPostTarget = false, addProjects = false)

      val params = p.targetProjectParams(target, options.hidden, Seq(), Seq()).copy(settings = Seq())
      target.mkProject(this, params, options)
    }
  }


}
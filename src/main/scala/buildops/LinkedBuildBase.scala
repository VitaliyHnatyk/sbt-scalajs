package com.inthenow.sbt.scalajs


import sbt._
import SbtScalajs._

object LinkedBuildBase {

  abstract class Std(m: CrossModuleOps, buildName: String) extends BuildOps(m, buildName) {



    def mkLinkedProject(target: TargetOps, buildInit: () =>Seq[Setting[_]], projects: Project*): Project = {
      val p = target.projectOps
      val options = p.targetProjectOptions.copy(hidden= true, copyProject=true)

      val params = p.targetProjectParams(target, options.hidden, p.projectNameSettings(target), projects)
        .copy(buildInit = buildInit)

      target.mkProject(this, params, options)
    }
  }

  abstract class SharedOps(m: CrossModuleOps, buildName: String) extends SharedBuildOps(m, buildName) {
    def mkProject(target: TargetOps, projects: Seq[Project]): Project = {
      val p = target.projectOps
      val options = p.targetProjectOptions.copy(hidden= true, callPostTarget = false)

      val params = p.targetProjectParams(target, options.hidden, noRootSettings, projects)

      target.mkProject(this, params, options)
    }

  }
}
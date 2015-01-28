package com.inthenow.sbt.scalajs
 
import sbt._

object SharedBuildBase {

  abstract class Std(m: CrossModuleOps, buildName: String) extends BuildOps(m, buildName) {
    def mkProject(target: TargetOps, projects: Seq[Project]): Project = {
      val p = target.projectOps
      val options = p.targetProjectOptions
      val params = p.targetProjectParams(target, options.hidden, p.projectNameSettings(target), projects)

      target.mkProject(this, params, options)
    }
  }

  abstract class SharedOps(m: CrossModuleOps, buildName: String) extends SharedBuildOps(m, buildName) {
    def mkProject(target: TargetOps, projects: Seq[Project]): Project = {
      val p = target.projectOps
      val options = p.targetProjectOptions.copy(hidden = true, addProjects = false)
      val params = p.targetProjectParams(target, options.hidden, p.projectNameSettings(target), projects)

      target.mkProject(this, params, options)
    }
  }
}

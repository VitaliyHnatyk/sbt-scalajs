package com.inthenow.sbt.scalajs
 
import sbt._

object SharedBuildBase {

  def mkStdProject(b:BuildOps, target: TargetOps, projects: Seq[Project]): Project = {
    val p = target.projectOps
    val options = p.targetProjectOptions
    val params = p.targetProjectParams(target, options.hidden, p.projectNameSettings(target), projects)

    target.mkProject(b, params, options)
  }

  abstract class Std(m: CrossModuleOps, buildName: String)( implicit log: Logger) extends BuildOps(m, buildName) {
    def mkProject(target: TargetOps, projects: Seq[Project]): Project =  mkStdProject(this, target, projects)
  }

  abstract class EmptyOps(m: CrossModuleOps, buildName: String)( implicit log: Logger) extends EmptyBuildOps(m, buildName) {

    def mkProject(target: TargetOps, projects: Seq[Project]): Project =  mkStdProject(this, target, projects)
  }

  abstract class SharedOps(m: CrossModuleOps, buildName: String)( implicit log: Logger) extends SharedBuildOps(m, buildName) {
    def mkProject(target: TargetOps, projects: Seq[Project]): Project = {
      val p = target.projectOps
      val options = p.targetProjectOptions.copy(hidden = true)
      val params = p.targetProjectParams(target, options.hidden, p.projectNameSettings(target), projects)

      target.mkProject(this, params, options)
    }
  }
}

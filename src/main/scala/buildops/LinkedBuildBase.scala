package com.inthenow.sbt.scalajs


import sbt._
import sbt.Keys._
import SbtScalajs._

object LinkedBuildBase {

  def mkStdLinkedProject(b:BuildOps,target: TargetOps, buildInit: () =>Seq[Setting[_]], projects: Seq[Project]): Project = {
    val p = target.projectOps
    val options = p.targetProjectOptions.copy(copyProject=true, addProjects = false)

    val settings:Seq[Setting[_]] = projects.map(d => aggregate in d.project := false)

    val params = p.targetProjectParams(target, options.hidden, p.projectNameSettings(target) ++  settings, projects)
      .copy(buildInit = buildInit)

    target.mkProject(b, params, options)
  }

  abstract class SharedOps(m: CrossModuleOps, buildName: String)( implicit log: Logger) extends SharedBuildOps(m, buildName) {
    def mkProject(target: TargetOps, projects: Seq[Project]): Project = {
      val p = target.projectOps
      val options = p.targetProjectOptions.copy(hidden= true, callPostTarget = false, addProjects = false)

      val params = p.targetProjectParams(target, options.hidden, Seq(), Seq()).copy(settings = Seq())
      target.mkProject(this, params, options)
    }
  }

  abstract class Std(m: CrossModuleOps, buildName: String)( implicit log: Logger) extends BuildOps(m, buildName) {

    def mkLinkedProject(target: TargetOps, buildInit: () =>Seq[Setting[_]], projects: Seq[Project]): Project = {
      mkStdLinkedProject(this, target, buildInit, projects)
    }
  }

  abstract class EmptyOps(m: CrossModuleOps, buildName: String)( implicit log: Logger) extends EmptyBuildOps(m, buildName) {

    def mkLinkedProject(target: TargetOps, buildInit: () => Seq[Setting[_]], projects: Seq[Project]): Project = {
      mkStdLinkedProject(this, target, buildInit, projects)
    }
  }
}
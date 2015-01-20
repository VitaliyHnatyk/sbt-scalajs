package com.inthenow.sbt.scalajs

import sbt._
import sbt.Keys._

class  ProjectOps(b: BuildOps) {

  implicit class ProjectOps(p: Project) {
    def addProjects(projects: Project*): Project = {
      val pr: Seq[ProjectReference] = projects.map(d => d.project)
      val cpd = projects.map(d => ClasspathDependency(d, Some("compile;test->test")))
      p.dependsOn(cpd: _*).aggregate(pr: _*)
    }
  }

  def mkModuleProject(t: Target, p: Project*): Project = {

    val settings = SbtScalajs.noRootSettings ++ Seq(name := {
      b.moduleOps.getModuleName()
    })

    mkBaseProject(settings).addProjects(p: _*)
  }

  def mkBaseProject(settings: Seq[Setting[_]]): Project = {
    val m = b.moduleOps
    Project(
      id = m.module.id,
      base = m.base,
      settings = m.getDefaultSettings ++ settings
    )
  }

  def mkTargetProject(t: Target, settings: Seq[Setting[_]] ): Project = {
    val m = b.moduleOps
    Project(
      id = m.getProjectId(m.module.id, t.name),
      base = m.getProjectBase(m.module.id, t.name),
      settings = m.getDefaultSettings ++ t.settings ++ settings ++ Seq(name := {
        m.getProjectName(m.module.id, t.name)
      }))
  }

  def mkSubProject(tp: Target, p: Project*): Project = tp.mkProject(mkTargetProject(tp, Seq()).addProjects(p:_*))

}

object ProjectOps {
  def apply(b: BuildOps)  = new ProjectOps(b)

}

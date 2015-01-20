package com.inthenow.sbt.scalajs

import sbt.Keys._
import sbt._

abstract class SharedBuildOpsBase(m: ModuleOps, buildName:String ) extends CrossBuildOps(m, buildName)

abstract class SharedBuildProjectOpsBase(b: CrossBuildOps  ) extends CrossProjectOps(b) {
  def mkProject(tp: Target, projects: Project*): Project = {
    tp.mkProject(mkTargetProject(tp, Seq()).addProjects(projects: _*))
  }

  def mkSharedProject(tp: Target, hidden: Boolean, projects: Project*): Project = {
    val settings = Seq(name := { b.getSharedProjectName(b.moduleOps.module.id, tp.name)})
    tp.mkProject(mkSharedProject(tp, hidden, settings, projects: _*))
  }
  def mkSharedProject(tp: Target,  cpd: Project*): Project =  mkSharedProject(tp, true, cpd:_*)

}

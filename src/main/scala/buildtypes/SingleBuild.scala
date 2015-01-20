package com.inthenow.sbt.scalajs

import sbt._
import sbt.Keys._

class SingleBuild(m: ModuleOps) extends BuildOps(m, "SingleBuild") {
  def project(t: Target, projects: Project*): Project = t.mkProject(this, projects: _*)
}

case object SingleBuild extends BuildType {
  def apply(m: ModuleOps) = new SingleBuild(m)
}

case class SingleBuildProjectOps(b: BuildOps) extends ProjectOps(b) {
  
  def mkProject(tp: Target, projects: Project*): Project = {
    val settings = tp.settings ++ Seq(name := {  b.moduleOps.getProjectName( b.moduleOps.module.id, tp.name)})
    tp.mkProject(mkBaseProject(settings))
  }
}


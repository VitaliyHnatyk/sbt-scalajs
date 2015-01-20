package com.inthenow.sbt.scalajs

import sbt._

class SharedBuild(m: ModuleOps) extends SharedBuildOpsBase(m, "SharedBuild") {
  def project(t: Target, projects: Project*): Project = t.mkProject(this, projects:_*)
}

case object SharedBuild extends BuildType {
  def apply(m: ModuleOps) = new SharedBuild(m)
}

case class  SharedBuildProjectOps(b: CrossBuildOps) extends SharedBuildProjectOpsBase(b) {}
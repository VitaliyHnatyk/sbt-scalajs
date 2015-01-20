package com.inthenow.sbt.scalajs

import sbt._

class CommonBaseBuild(m: ModuleOps) extends SharedBuildOpsBase(m, "CommonBaseBuild") {
  def project(t: Target, cpd: Project*): Project = t.mkProject(this, cpd: _*)

  override def getSharedProjectBase(projectId: String, projectDir: String, hidden: Boolean = false): File = {
    m.base / m.sharedLabel
  }
}

case object CommonBaseBuild extends BuildType {
  def apply(m: ModuleOps) = new CommonBaseBuild(m)
}

case class  CommonBaseBuildProjectOps(b: CrossBuildOps) extends SharedBuildProjectOpsBase(b) {

}
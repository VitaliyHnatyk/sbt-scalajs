package com.inthenow.sbt.scalajs

import sbt._

class CommonBaseBuild(m: CrossModuleOps) extends SharedBuildBase.Std(m, "CommonBaseBuild")

case object CommonBaseBuild extends SharedBuildType {

  def getBuildOps(m: CrossModuleOps,projectType:Standard ) =  new CommonBaseBuild(m)
  def getBuildOps(m: CrossModuleOps,projectType:Shared ) =  new SharedOps(m)

  class SharedOps(m: CrossModuleOps) extends SharedBuildBase.SharedOps(m, "CommonBaseBuild") {

    override def getProjectBase(projectId: String, projectDir: String, hidden: Boolean = false): File = {
      m.getBase / m.getSharedLabel
    }
  }
}

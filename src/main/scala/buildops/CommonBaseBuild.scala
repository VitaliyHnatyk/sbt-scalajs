package com.inthenow.sbt.scalajs

import sbt._

class CommonBaseBuild(m: CrossModuleOps)( implicit log: Logger) extends SharedBuildBase.Std(m, "CommonBaseBuild")

case object CommonBaseBuild extends SharedBuildType {
  def getBuildOps(m: CrossModuleOps, projectType: Empty)(implicit log: Logger): BuildOps = new EmptyOps(m)

  def getBuildOps(m: CrossModuleOps, projectType: Standard)(implicit log: Logger) = new CommonBaseBuild(m)

  def getBuildOps(m: CrossModuleOps, projectType: Shared)(implicit log: Logger) = new SharedOps(m)

  class SharedOps(m: CrossModuleOps)(implicit log: Logger) extends SharedBuildBase.SharedOps(m, "CommonBaseBuild") {

    override def getProjectBase(projectId: String, projectDir: String, hidden: Boolean = false): File = {
      m.getBase / m.getSharedLabel
    }
  }

  class EmptyOps(m: CrossModuleOps)(implicit log: Logger) extends SharedBuildBase.EmptyOps(m, "CommonBaseBuild")
}

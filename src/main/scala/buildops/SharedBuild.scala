package com.inthenow.sbt.scalajs

import sbt._

class SharedBuild(m: CrossModuleOps) extends SharedBuildBase.Std(m, "SharedBuild") {
}

case object SharedBuild extends SharedBuildType {
  def getBuildOps(m: CrossModuleOps,projectType:Standard) = new SharedBuild(m)
  def getBuildOps(m: CrossModuleOps,projectType:Shared) = new SharedOps(m)

  class SharedOps (m: CrossModuleOps) extends SharedBuildBase.SharedOps(m, "SharedBuild") {
  }
}


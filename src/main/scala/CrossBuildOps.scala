package com.inthenow.sbt.scalajs

import sbt._

abstract class CrossBuildOps(m: ModuleOps, buildName:String) extends BuildOps(m, buildName) {

  def getSharedProjectBase(projectId: String, projectDir: String, hidden: Boolean ): File = {
    if (hidden) m.base / s".${m.sharedLabel}_$projectDir" else m.base / m.sharedLabel
  }

  def getSharedProjectId(projectId: String, projectDir: String) = {
    s"${m.module.id}_${m.sharedLabel}_$projectDir"
  }

  def getSharedProjectName(projectId: String, projectDir: String) = {
    s"${m.module.modulePrefix}${projectId}_${m.sharedLabel}"
  }
}







package com.inthenow.sbt.scalajs

import sbt._
import sbt.Keys._

class XCommonBaseBuild extends BuildSig {
  type BuildOps = XCommonBaseBuild.XCommonBaseBuildOps[Targets]
  type Targets  = XTargets
}

object XCommonBaseBuild {
  // use these ops to create a module with shared code compiled to separate artifacts
  implicit class XCommonBaseBuildOps[T <: XTargetsSig](m: XModule[T, XCommonBaseBuildOps[T]])
    extends XSharedBuildOpsBase[T, XCommonBaseBuildOps[T]](m) {

    override def jsShared(shared: Project): Project = xShared(m.targets.js, true).enablePlugins(SbtScalajs)

    override def getSharedProjectBase(projectId: String, projectDir: String, hidden: Boolean = false): File = {
      m.base / m.sharedLabel
    }
  }
}


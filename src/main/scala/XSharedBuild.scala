package com.inthenow.sbt.scalajs


class XSharedBuild extends BuildSig {

  type BuildOps = XSharedBuild.XSharedBuildOps[Targets]

  type Targets = XTargets

}

object XSharedBuild {
  // use these ops to create a module with shared code compiled to separate artifacts
  implicit class XSharedBuildOps[T <: XTargetsSig](m: XModule[T, XSharedBuildOps[T]])
    extends XSharedBuildOpsBase[T, XSharedBuildOps[T]](m)
}


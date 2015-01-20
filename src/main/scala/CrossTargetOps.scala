package com.inthenow.sbt.scalajs

import sbt._

trait CrossTargetOps extends TargetOps{

  override def mkSharedProject(t: Target, b: SharedBuild, cpd: Project*): Project = {
    SharedBuildProjectOps(b).mkSharedProject(t, cpd: _*)
  }

  override def mkSymLinkedProject(t: Target, b: SymLinkedBuild, cpd: Project*): Project = {
    SymLinkedBuildProjectOps(b) mkSharedProject(t, cpd: _*)
  }

  override def mkSbtLinkedProject(t: Target, b: SbtLinkedBuild, cpd: Project*): Project = {
    SbtLinkedBuildProjectOps(b).mkSharedProject(t, cpd: _*)
  }

  override def mkCommonBaseProject(t: Target, b: CommonBaseBuild, cpd: Project*): Project = {
    CommonBaseBuildProjectOps(b).mkSharedProject(t, cpd: _*)
  }
}

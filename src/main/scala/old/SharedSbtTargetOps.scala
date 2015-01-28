package com.inthenow.sbt.scalajs

import sbt._
import com.inthenow.sbt.scalajs.SbtScalajs._





/*
class SharedTargetOps(targetType:TargetType, id: String, name: String, settings: Seq[Def.Setting[_]],projectOps: ProjectOps)
extends TargetOps(targetType, id, name, settings, projectOps){
  override  def getDefaultSettings(m: CrossModuleOps, t: TargetOps): Seq[Def.Setting[_]] = {
    super.getDefaultSettings(m,t) ++ CrossVersionSharedSources(m.getSharedLabel)
  }

  */

/*
  override def mkSharedBuildProject(t: Target, b: SharedSbtBuild, cpd: Project*): Project = {
    SharedBuildProjectOps(b).mkSharedProject(t, cpd: _*)
  }

  override def mkSymLinkedProject(t: Target, b: SymLinkedSbtBuild, cpd: Project*): Project = {
    SymLinkedBuildProjectOps(b) mkSharedProject(t, cpd: _*)
  }

  override def mkSbtLinkedProject(t: Target, b: SbtLinkedSbtBuild, cpd: Project*): Project = {
    SbtLinkedBuildProjectOps(b).mkSharedProject(t, cpd: _*)
  }

  override def mkCommonBaseProject(t: Target, b: CommonBaseSbtBuild, cpd: Project*): Project = {
    CommonBaseBuildProjectOps(b).mkSharedProject(t, cpd: _*)
  }

  */


package com.inthenow.sbt.scalajs

import com.inthenow.sbt.scalajs.SbtScalajs._
import sbt._

case class Target(t: TargetType, id: String, name: String, settings: Seq[Def.Setting[_]], sharedSettings: Seq[Def.Setting[_]]) {

  def mkProject(p: Project) = t.mkProject(p)
  def mkProject(b: RootBuild, p: Project*):Project       = t.mkRootProject(this, b, p: _*)
  def mkProject(b: SingleBuild, p: Project*):Project     = t.mkSingleProject(this, b, p: _*)
  def mkProject(b: SharedBuild, p: Project*):Project     = t.mkSharedProject(this, b, p: _*)
  def mkProject(b: CommonBaseBuild, p: Project*):Project = t.mkCommonBaseProject(this, b, p: _*)
  def mkProject(b: SymLinkedBuild, p: Project*):Project  = t.mkSymLinkedProject(this, b, p: _*)
  def mkProject(b: SbtLinkedBuild, p: Project*):Project  = t.mkSbtLinkedProject(this, b, p: _*)

}

object Target {
  def apply(m: ModuleOps, t: Target ): Target = {
    val tt = t.t
    Target(t.t, t.id, t.name, t.settings ++ tt. getDefaultSettings(m, t), tt.getDefaultSharedSettings(m,t ))
  }
}

trait TargetOps {
  def getDefaultSettings(m: ModuleOps, t: Target): Seq[Def.Setting[_]] = {
    scalajsTargetSettings(t.name) ++ CrossVersionSources()
  }

  def getDefaultSharedSettings(m: ModuleOps, t: Target): Seq[Def.Setting[_]] = {
    CrossVersionSharedSources(m.sharedLabel)
  }

  def mkProject(p: Project): Project = p

  def mkRootProject(t: Target, b: RootBuild, cpd: Project*): Project = {
    RootBuildProjectOps(b).mkProject(t, cpd: _*)
  }

  def mkSingleProject(t: Target, b: SingleBuild, cpd: Project*): Project = {
    SingleBuildProjectOps(b).mkProject(t, cpd: _*)
  }

  def mkSharedProject(t: Target, b: SharedBuild, cpd: Project*): Project = {
    SharedBuildProjectOps(b).mkProject(t, cpd: _*)
  }

  def mkSymLinkedProject(t: Target, b: SymLinkedBuild, cpd: Project*): Project = {
    SymLinkedBuildProjectOps(b) mkProject(t, cpd: _*)
  }

  def mkSbtLinkedProject(t: Target, b: SbtLinkedBuild, cpd: Project*): Project = {
    SbtLinkedBuildProjectOps(b).mkProject(t, cpd: _*)
  }

  def mkCommonBaseProject(t: Target, b: CommonBaseBuild, cpd: Project*): Project = {
    CommonBaseBuildProjectOps(b).mkProject(t, cpd: _*)
  }
}

trait TargetType extends TargetOps {
  def apply(id: String = "",
            name: String = "",
            settings: Seq[Def.Setting[_]] = Seq(),
            sharedSettings: Seq[Def.Setting[_]] = Seq()): Target
}










package com.inthenow.sbt.scalajs


import sbt._
import sbt.Keys._

trait SBuildSig  {
  type Targets  <: XTarget
  type BuildOps <: SBuild.SBuildOps[Targets]
}

trait SBuildJs extends  SBuildSig   {
  type BuildOps = SBuild.SBuildOps[Targets]
  type Targets  = JsTarget
}

trait SBuildCommonJs extends SBuildSig   {
  type BuildOps = SBuild.SBuildOps[Targets]
  type Targets  = CommonJsTarget
}

trait SBuildJvm extends SBuildSig  {
  type BuildOps = SBuild.SBuildOps[Targets]
  type Targets  = JvmTarget
}

object SBuild {

  // use these ops to create a module with a single target
  implicit class SBuildOps[T <: XTarget](m: SModule[T, SBuildOps[T]]) extends SingleBuildOps[T] {

    def jvmProject[T <: JvmTarget](implicit tp: T): Project = mkProject(tp)

    def jsProject[T <: JsTarget](implicit tp: T): Project  = mkProject(tp).enablePlugins(SbtScalajs)

    protected def mkProject[T<: XTarget](tp: T): Project = {
      Project(
        id = m.id,
        base = m.base,
        settings = m.getDefaultSettings ++ tp.settings ++ Seq(name := {
          m.getProjectName(m.id, tp.name)
        })
      )
    }

  }
}
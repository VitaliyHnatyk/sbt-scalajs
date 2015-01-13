package com.inthenow.sbt.scalajs


import sbt._
import sbt.Keys._


case class CrossModule(id: String,
                   build: CrossBuildType,
                   target: TargetsType = XTargets,
                   baseDir: String = ".",
                   modulePrefix: String = "",
                   sharedLabel: String = "shared",
                   defaultSettings: Seq[Def.Setting[_]] = Seq())
                  ( implicit val log: Logger)  extends DefaultModuleOps {

  import CrossBuildOps._
  val targets = target.targets
  val jvm = targets.jvm
  val js = targets.js
  val commonJs = targets.commonJs

  val ops = getBuildType(this).ops(this)

  def jvmProject(p: Project): Project = ops.jvmProject(p)

  def jsProject(p: Project): Project = ops.jsProject(p)

  def jvmShared() = ops.jvmShared()

  def jsShared(p: Project): Project = ops.jsShared(p)

  def project(jvm: Project, js: Project): Project =
    Project(
      id = id,
      base = base,
      settings = SbtScalajs.noRootSettings ++ getDefaultSettings ++ Seq(name := {
        getModuleName()
      })
    ).dependsOn(jvm, js).aggregate(jvm, js)

}


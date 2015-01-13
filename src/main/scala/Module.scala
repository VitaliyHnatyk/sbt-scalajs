package com.inthenow.sbt.scalajs

import sbt._

case class Module(id: String,
                  build: BuildType = SingleBuild,
                  target: TargetType,
                  baseDir: String = ".",
                  modulePrefix: String = "",
                  sharedLabel: String = "",
                  defaultSettings: Seq[Def.Setting[_]] = Seq())
  extends DefaultModuleOps {

  import BuildOps._

  val tp:Target = target.target
  val ops = getBuildType(id, build).ops(this)

  def jvmProject(): Project = ops.jvmProject()

  def jsProject(): Project = ops.jsProject()
}


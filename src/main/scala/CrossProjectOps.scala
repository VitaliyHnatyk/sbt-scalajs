package com.inthenow.sbt.scalajs

import sbt._

class CrossProjectOps(b:CrossBuildOps) extends ProjectOps(b) {
  val mOps  = b.moduleOps

  def mkSharedProject(tp: Target, hidden: Boolean, settings: Seq[Setting[_]], cpd: Project*): Project = {
    Project(
      id = b.getSharedProjectId(mOps.module.id, tp.name),
      base = b.getSharedProjectBase(mOps.module.id, tp.name, hidden),
      settings = settings ++ mOps.getDefaultSettings ++ tp.settings
    )
  }
}
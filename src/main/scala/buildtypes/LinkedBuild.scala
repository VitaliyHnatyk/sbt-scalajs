package com.inthenow.sbt.scalajs

import sbt._
import SbtScalajs._

abstract class LinkedBuild(m: ModuleOps, getName:String ) extends CrossBuildOps(m, getName)

abstract class LinkedBuildProjectOps(b: CrossBuildOps ) extends CrossProjectOps(b) {
  def mkLinkedProject(tp: Target, links: Seq[FileSettings], projects: Project*): Project = {
    val p: Project = projects.head
    val newSettings: Seq[sbt.Def.Setting[_]] = tp.settings ++ links ++ tp.sharedSettings

    tp.mkProject(mkTargetProject(tp, newSettings).addProjects(p).enablePlugins(p.plugins))
  }

  def mkSharedProject(tp: Target, cpd: Project*): Project = {
    mkSharedProject(tp, true, SbtScalajs.noRootSettings, cpd: _*)
  }
}

package com.inthenow.sbt.scalajs

import sbt._
import SbtScalajs._

class SbtLinkedBuild(m: ModuleOps) extends LinkedBuild(m, "SbtLinkedBuild") {
  def project(t: Target, projects: Project*): Project = t.mkProject(this, projects:_*)
}

case object SbtLinkedBuild extends BuildType {
  def apply(m: ModuleOps) = new SbtLinkedBuild(m)
}

case class  SbtLinkedBuildProjectOps(b: CrossBuildOps) extends LinkedBuildProjectOps(b) {
  def mkProject(tp: Target, projects:  Project* ): Project = {
    val p = projects.head
    val links = linkedSources(p)
    mkLinkedProject(tp, links, p)
  }
}

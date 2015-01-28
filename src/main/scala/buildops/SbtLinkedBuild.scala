package com.inthenow.sbt.scalajs

import sbt._
import SbtScalajs._

class SbtLinkedBuild(m: CrossModuleOps) extends LinkedBuildBase.Std(m, "SbtLinkedBuild") {

  def mkProject(t: TargetOps, projects: Seq[Project]): Project = {
    val p = projects.head
    def init():Seq[Setting[_]] = {
      linkedSources(p)
    }
    mkLinkedProject(t, init, p)
  }
}

case object SbtLinkedBuild extends BuildType{
  def getBuildOps(m: CrossModuleOps, projectType: Standard)= new SbtLinkedBuild(m)
  def getBuildOps(m: CrossModuleOps, projectType: Shared) = new SharedOps(m)

  class SharedOps(m: CrossModuleOps) extends LinkedBuildBase.SharedOps(m, "SbtLinkedBuild") {
  }
}

 

package com.inthenow.sbt.scalajs

import sbt._

abstract class BuildOps(val moduleOps: ModuleOps, val buildName:String) {
  def project(t: Target, cpd:Project*): Project
}

trait BuildType {
  def apply(m: ModuleOps): BuildOps
}




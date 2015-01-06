package com.inthenow.sbt.scalajs

import sbt._

trait BuildSig {
  type Targets
  type BuildOps
}

trait BuildOps[TargetsSig] {
  type SharedProject

  def getSharedProjectName(projectId: String, projectDir: String): String

  def getSharedProjectId(projectId: String, projectDir: String): String

  def getSharedProjectBase(projectId: String, projectDir: String, hidden: Boolean = false): File

  def jvmProject(p: SharedProject): Project

  def jsProject(p: SharedProject): Project

  def jvmShared(): SharedProject

  def jsShared(shared: SharedProject): SharedProject
}

trait SingleBuildOps[TargetsSig]
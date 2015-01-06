package com.inthenow.sbt.scalajs

import sbt._
import sbt.Keys._

class XLinkedBuild extends BuildSig {

  type BuildOps = XLinkedBuild.XLinkedBuildOps[Targets]

  type Targets = XTargets

}

object XLinkedBuild {

  // use these ops to create a module with real shared code using symbolic links
  implicit class XLinkedBuildOps[T <: XTargetsSig](m: XModule[T, XLinkedBuildOps[T]]) extends BuildOps[T] {
    type SharedProject = ProjectProxy

    def jvmShared(): SharedProject = new ProjectProxy

    def jsShared(shared: SharedProject): SharedProject = new ProjectProxy

    def getSharedProjectBase(projectId: String, projectDir: String, hidden: Boolean = false): File = file("")

    def getSharedProjectId(projectId: String, projectDir: String) = ""

    def getSharedProjectName(projectId: String, projectDir: String) = ""

    def jvmProject(p: SharedProject) = xProject(p, m.targets.jvm)

    def jsProject(p: SharedProject) = xProject(p, m.targets.js).enablePlugins(SbtScalajs)

    private def xProject(p: SharedProject, tp: XTarget): Project = {
      // if we can find a shared directory, link to it
      SbtScalajs.linkToShared(m.getProjectBase(m.id, tp.name), s"../${m.sharedLabel}")(m.log)

      // Create the project
      Project(
        id = m.getProjectId(m.id, tp.name),
        base = m.getProjectBase(m.id, tp.name),
        settings = m.getDefaultSettings ++ tp.settings ++ Seq(name := {
          m.getProjectName(m.id, tp.name)
        })
      ).settings(p.settings: _*)
        .aggregate(p.aggregate: _*)
        .dependsOn(p.dependencies: _*)
        .enablePlugins(p.enablePlugins: _*)
        .disablePlugins(p.disablePlugins: _*)
    }
  }

}
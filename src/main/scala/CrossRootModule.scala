package com.inthenow.sbt.scalajs

import sbt._
import sbt.Keys._
import SbtScalajs._

case class CrossRootModule(moduleName: String,
                           baseDir: String = ".",
                           target: TargetsType = XTargets,
                           sharedLabel: String = "",
                           defaultSettings: Seq[Def.Setting[_]] = Seq()) {
  val targets = target.targets
  val jvm = targets.jvm
  val js = targets.js
  val commonJs = targets.commonJs

  val jvmName = s"${moduleName}${jvm.id}"
  val jsName = s"${moduleName}${js.id}"

  def jvmProject(cpd: ClasspathDep[ProjectReference]*): Project = mkProject(jvmName, jvm.settings, cpd: _*)

  def jsProject(cpd: ClasspathDep[ProjectReference]*): Project = {
    mkProject(jsName, js.settings, cpd: _*).enablePlugins(SbtScalajs)
  }

  def project(jvm: Project, js: Project): Project = Project(
    id = s"${moduleName}Root",
    base = file(baseDir),
    settings = defaultSettings ++ SbtScalajs.noPublishSettings
  ).dependsOn(jvm, js)
    .aggregate(jvm, js)

  protected def mkProject(label: String, targetSettings: Seq[Setting[_]], cpd: ClasspathDep[ProjectReference]*): Project = {
    val p: Seq[ProjectReference] = cpd.map(d => d.project)
    Project(
      id = label,
      base = file(s".$label"),
      settings = defaultSettings ++ targetSettings ++ Seq(name := moduleName)
    ).dependsOn(cpd: _*).aggregate(p: _*)
  }
}

package com.inthenow.sbt.scalajs

import sbt._
import sbt.Keys._
import SbtScalajs._

case class XRootModule(id: String = "root",
                       baseDir: String = ".",
                       moduleName: String,
                       sharedLabel: String = "",
                       defaultSettings: Seq[Def.Setting[_]] = Seq()) {

  val jvmName = s"${moduleName}Jvm"
  val jsName = s"${moduleName}Js"

  def jvmProject(cpd: ClasspathDep[ProjectReference]*): Project = mkProject(jvmName, scalajsJvmSettings, cpd:_*)

  def jsProject(cpd: ClasspathDep[ProjectReference]*): Project = {
    mkProject(jsName, scalajsJvmSettings, cpd:_*).enablePlugins(SbtScalajs)
  }

  def project(jvm: Project, js: Project): Project = Project(
    id = id,
    base = file(baseDir),
    settings = defaultSettings ++ SbtScalajs.noRootSettings
  ).dependsOn(jvm , js )
    .aggregate(jvm , js )

  protected def mkProject(label:String, targetSettings:Seq[Setting[_]], cpd: ClasspathDep[ProjectReference]*): Project ={
    val p:Seq[ProjectReference] = cpd.map(d => d.project)
    Project(
      id = label,
      base = file(s".$label"),
      settings = defaultSettings ++ targetSettings ++ Seq(name := moduleName)
    ).dependsOn(cpd: _*).aggregate(p: _*)
  }
}

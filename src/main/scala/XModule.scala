package com.inthenow.sbt.scalajs


import sbt._
import sbt.Keys._

case class XModule(id: String,
                   baseDir: String = ".",
                   sharedLabel:String = "shared",
                   defaultSettings: Seq[Def.Setting[_]] = Seq())
                  (implicit jvmTarget: JvmTarget, jsTarget: JsTarget) {

  lazy val base = file(baseDir)

  def getModuleName() = {
    s"${id}_module"
  }

  def getProjectBase(projectId: String, projectDir: String, hidden:Boolean = false):File = {
    if (hidden) base / s".$projectDir" else base / projectDir
  }

  def getProjectId(projectId: String, projectDir: String) = {
    s"${id}_$projectDir"
  }

  def getProjectName(projectId: String, projectDir: String) = {
    s"${id}"
  }

  def getSharedProjectBase(projectId: String, projectDir: String, hidden:Boolean = false):File = {
    if (hidden) base / s".${sharedLabel}_$projectDir" else base / sharedLabel
  }

  def getSharedProjectId(projectId: String, projectDir: String) = {
    s"${id}_${sharedLabel}_$projectDir"
  }

  def getSharedProjectName(projectId: String, projectDir: String) = {
    s"${id}_${sharedLabel}"
  }

  def getDefaultSettings: Seq[Def.Setting[_]] = defaultSettings

  def project(jvm: Project, js: Project): Project =
    Project(
      id = id,
      base = base,
      settings = SbtScalajs.noRootSettings ++ getDefaultSettings ++ Seq( name := { getModuleName() } )
    ).dependsOn(jvm, js).aggregate(jvm, js)

  def jvmProject(depends: Project) = xProject(depends, jvmTarget)

  def jsProject(depends: Project) = xProject(depends, jsTarget).enablePlugins(SbtScalajs)

  def jvmShared(): Project = xShared(jvmTarget)

  def jsShared(shared: Project): Project = xShared(jsTarget, true).enablePlugins(SbtScalajs).settings(SbtScalajs.linkedSources(shared): _*)


  def xProject(depends: Project, tp: XTarget): Project =
    Project(
      id = getProjectId(id, tp.name),
      base = getProjectBase(id, tp.name),
      settings = getDefaultSettings ++ tp.settings ++ Seq( name := { getProjectName(id, tp.name) } )
    ).dependsOn(depends).aggregate(depends)

  def xShared(tp: XTarget, hidden:Boolean = false): Project =
    Project(
      id = getSharedProjectId(id, tp.name),
      base = getSharedProjectBase(id, tp.name, hidden) ,
      settings = getDefaultSettings ++ tp.settings ++ Seq( name := { getSharedProjectName(id, tp.name) } )
    )

}


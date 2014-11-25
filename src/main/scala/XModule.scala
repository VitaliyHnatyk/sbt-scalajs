package com.inthenow.sbt.scalajs


import sbt._

case class XModule(id: String,
                   baseDir: String = ".",
                   sharedLabel:String = "shared",
                   defaultSettings: Seq[Def.Setting[_]] = Seq())
                  (implicit jvmTarget: JvmTarget, jsTarget: JsTarget) {

  lazy val base = file(baseDir)

  def getProjectBase(projectId: String, projectDir: String, hidden:Boolean = false):File = {
    if (hidden) base / s".$projectDir" else base / projectDir
  }

  def getProjectId(projectId: String, projectDir: String) = {
    s"${id}_$projectDir"
  }

  def getSharedProjectBase(projectId: String, projectDir: String, hidden:Boolean = false):File = {
    if (hidden) base / s".${sharedLabel}_$projectDir" else base / sharedLabel
  }

  def getSharedProjectId(projectId: String, projectDir: String) = {
    s"${id}_${sharedLabel}_$projectDir"
  }

  def getDefaultSettings: Seq[Def.Setting[_]] = defaultSettings

  def project(jvm: Project, js: Project): Project =
    Project(
      id = id,
      base = base,
      settings = SbtScalajs.noRootSettings ++ getDefaultSettings
    ).dependsOn(jvm, js).aggregate(jvm, js)

  def jvmProject(depends: Project) = xProject(depends, jvmTarget)

  def jsProject(depends: Project) = xProject(depends, jsTarget).enablePlugins(SbtScalajs)

  def jvmShared(): Project = xShared(jvmTarget)

  def jsShared(shared: Project): Project = xShared(jsTarget, true).enablePlugins(SbtScalajs).settings(SbtScalajs.linkedSources(shared): _*)


  def xProject(depends: Project, tp: XTarget): Project =
    Project(
      id = getProjectId(id, tp.name),
      base = getProjectBase(id, tp.name),
      settings = getDefaultSettings ++ tp.defaultSettings
    ).dependsOn(depends).aggregate(depends)

  def xShared(tp: XTarget, hidden:Boolean = false): Project =
    Project(
      id = getSharedProjectId(id, tp.name),
      base = getSharedProjectBase(id, tp.name, hidden) ,
      settings = getDefaultSettings ++ tp.defaultSettings
    )

}


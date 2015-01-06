package com.inthenow.sbt.scalajs


import sbt._

trait ModuleOps {
  val id: String
  val baseDir: String
  val modulePrefix: String
  val sharedLabel: String
  val defaultSettings: Seq[Def.Setting[_]]

  def base: File

  def getModuleName(): String

  def getProjectBase(projectId: String, projectDir: String, hidden: Boolean = false): File

  def getProjectId(projectId: String, projectDir: String): String

  def getProjectName(projectId: String, projectDir: String): String

  def getDefaultSettings: Seq[Def.Setting[_]]
}

trait DefaultModuleOps extends ModuleOps {
  def base = file(baseDir)

  def getModuleName(): String = s"${id}_module"

  def getProjectBase(projectId: String, projectDir: String, hidden: Boolean = false): File = {
    if (hidden) base / s".$projectDir" else base / projectDir
  }

  def getProjectId(projectId: String, projectDir: String) = s"${id}_$projectDir"

  def getProjectName(projectId: String, projectDir: String) = s"$modulePrefix$id"

  def getDefaultSettings: Seq[Def.Setting[_]] = defaultSettings
}

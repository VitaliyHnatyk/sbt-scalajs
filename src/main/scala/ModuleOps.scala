package com.inthenow.sbt.scalajs


import sbt._

trait ModuleOps {

  val module: CrossModule

  def base: File

  def getModuleName(): String

  def setBuildType(build: BuildType): String

  def getBuildType(default: BuildType): BuildType

  def getProjectBase(projectId: String, projectDir: String, hidden: Boolean = false): File

  def getProjectId(projectId: String, projectDir: String): String

  def getProjectName(projectId: String, projectDir: String): String

  def getDefaultSettings: Seq[Def.Setting[_]]

  def sharedLabel: String = module.sharedLabel


}

class DefaultModuleOps(val module: CrossModule) extends ModuleOps {

  def base = file(module.baseDir)

  def getModuleName(): String = s"${module.id}_module"

  def getProjectBase(projectId: String, projectDir: String, hidden: Boolean = false): File = {
    if (hidden) base / s".$projectDir" else base / projectDir
  }

  def getProjectId(projectId: String, projectDir: String) = s"${module.id}_$projectDir"

  def getProjectName(projectId: String, projectDir: String) = s"${module.modulePrefix}${module.id}"

  def getDefaultSettings: Seq[Def.Setting[_]] = module.defaultSettings

  def getKey() = s"${module.modulePrefix}${module.id}.build"

  def setBuildType(build: BuildType): String = {
    val key = getKey()
    val result = build(this).buildName
    System.setProperty(key, result)
    result
  }

  def getBuildType(default: BuildType): BuildType = {
    val key = getKey()
    val result = Option(System.getProperty(key)) match {
      case Some(name) => {
        val buildType = module.buildTypes.find(b => b(this).buildName == name)
        buildType.getOrElse(throw new Error(s"Unknown build type $name for $key"))
      }
      case None => default
    }
    result
  }
}

/*
class RootModuleOps(module: CrossModule) extends DefaultModuleOps(module) {

  def base = file(module.baseDir)

  def getModuleName(): String = s"${module.id}_module"

  def getProjectBase(projectId: String, projectDir: String, hidden: Boolean = false): File = {
    if (hidden) base / s".$projectDir" else base / projectDir
  }

  def getProjectId(projectId: String, projectDir: String) = s"${module.id}_$projectDir"

  def getProjectName(projectId: String, projectDir: String) = s"${module.modulePrefix}${module.id}"

  def getDefaultSettings: Seq[Def.Setting[_]] = module.defaultSettings

  def getKey() = s"${module.modulePrefix}${module.id}.build"

  def setBuildType(build: BuildType): String = {
    val key = getKey()
    val result = build(this).buildName
    System.setProperty(key, result)
    result
  }

  def getBuildType(default: BuildType): BuildType = {
    val key = getKey()
    val result = Option(System.getProperty(key)) match {
      case Some(name) => {
        val buildType = module.buildTypes.find(b => b(this).buildName == name)
        buildType.getOrElse(throw new Error(s"Unknown build type $name for $key"))
      }
      case None => default
    }
    result
  }
}
*/
trait ModuleType {
  def apply(m: CrossModule): ModuleOps
}

case object DefaultModule extends ModuleType {
  def apply(crossModule: CrossModule) = new DefaultModuleOps(crossModule)
}
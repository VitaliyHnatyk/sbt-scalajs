package com.inthenow.sbt.scalajs


import sbt.File
import sbt._

trait CrossModuleOps {

  val crossModule: CrossModule
  val moduleOps:CrossModuleOps

  def getModuleType:CrossModuleType
  def getBase: File

  def getModuleName(): String

  def setBuildType(build: BuildType): String

  def getBuildType(default: BuildType): BuildType

  def getDefaultSettings: Seq[Def.Setting[_]]

  def getSharedLabel: String

  def project(t: TargetType, projects: Project*): Project

  def project(t: TargetType, pt:ProjectType, projects: Project*): Project

  def project(t: Target, projects: Project*): Project

  def project(t: Target, projectType:ProjectType, projects: Project*): Project
}

class StdCrossModuleOps(val crossModule: CrossModule)( implicit val log: Logger) extends CrossModuleOps  {

  val moduleOps =  this

  def getModuleType:CrossModuleType = crossModule.moduleType

  def getBase = file(crossModule.baseDir)

  def getModuleName(): String = s"${crossModule.id}"

  def getSharedLabel: String = crossModule.sharedLabel

  def getDefaultSettings: Seq[Def.Setting[_]] = crossModule.defaultSettings

  def getKey() = s"${crossModule.modulePrefix}${crossModule.id}.build"

  def setBuildType(build: BuildType): String = {
    val key = getKey()
    val result = build.getBuildOps(this, Standard).buildName
    System.setProperty(key, result)
    result
  }

  def getBuildType(default: BuildType): BuildType = {
    val key = getKey()
    val result = Option(System.getProperty(key)) match {
      case Some(name) => {
        val buildType = crossModule.buildTypes.find(b => b.getBuildOps(this, Standard).buildName == name)
        buildType.getOrElse(throw new Error(s"Unknown build type $name for $key"))
      }
      case None => default
    }
    result
  }

  def project(t: TargetType, projects: Project*): Project = project(t(), projects: _*)

  def project(t: TargetType, pt:ProjectType, projects: Project*): Project = project(t(), pt, projects: _*)

  def project(t: Target, projects: Project*): Project = project(t, Standard,  projects: _*)

  def project(t: Target, projectType:ProjectType, projects: Project*): Project =  {
    val bt =moduleOps.getBuildType(crossModule.buildType)
    val buildOps = projectType.getBuildOps(moduleOps,bt)

    buildOps.project(t, projectType, projects)
  }
}

trait CrossModuleType {
  def apply(crossModule: CrossModule)( implicit log: Logger): CrossModuleOps
}
case object StdCrossModuleType extends CrossModuleType  {
  def apply(crossModule: CrossModule)( implicit log: Logger): CrossModuleOps = new StdCrossModuleOps(crossModule)
}
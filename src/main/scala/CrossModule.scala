package com.inthenow.sbt.scalajs

import sbt._

object CrossModule {
  def allBuildTypes: List[BuildType] = List(
    RootBuild, SingleBuild, SharedBuild, CommonBaseBuild, SbtLinkedBuild,SymLinkedBuild )
}

case class CrossModule(buildType: BuildType,
                       id: String,
                       moduleType: CrossModuleType = StdCrossModuleType,
                       buildTypes: List[BuildType] = CrossModule.allBuildTypes,
                       baseDir: String = ".",
                       modulePrefix: String = "",
                       sharedLabel: String = "shared",
                       defaultSettings: Seq[Def.Setting[_]] = Seq()) ( implicit val log: Logger) extends CrossModuleOps {

  val moduleOps = moduleType(this)

  val crossModule = moduleOps.crossModule

  val getModuleType: CrossModuleType = moduleOps.getModuleType

  def getBase: File = moduleOps.getBase

  def getModuleName(): String = moduleOps.getModuleName()

  def setBuildType(build: BuildType): String = moduleOps.setBuildType(build)

  def getBuildType(default: BuildType): BuildType = moduleOps.getBuildType(default)

  def getDefaultSettings: Seq[Def.Setting[_]] = moduleOps.getDefaultSettings

  def getSharedLabel: String = moduleOps.getSharedLabel

  def project(t: TargetType, projects: Project*): Project = moduleOps.project(t, projects: _*)

  def project(t: TargetType, pt: ProjectType, projects: Project*): Project = moduleOps.project(t, pt, projects: _*)

  def project(t: Target, projects: Project*): Project = moduleOps.project(t, projects: _*)

  def project(t: Target, projectType: ProjectType, projects: Project*): Project = moduleOps.project(t, projectType, projects: _*)
}


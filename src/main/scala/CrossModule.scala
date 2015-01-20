package com.inthenow.sbt.scalajs

import sbt._

object CrossModule {
  def allTypes = List(RootBuild, SingleBuild, SharedBuild, CommonBaseBuild, SymLinkedBuild, SbtLinkedBuild)
}

case class CrossModule(build: BuildType, id: String,
                       moduleType: ModuleType = DefaultModule,
                       buildTypes: List[BuildType] = CrossModule.allTypes,
                       baseDir: String = ".",
                       modulePrefix: String = "",
                       sharedLabel: String = "shared",
                       defaultSettings: Seq[Def.Setting[_]] = Seq()) ( implicit val log: Logger){
  val moduleOps =  moduleType(this)
  val ops = moduleOps.getBuildType(build)(moduleOps)

  def project(t: TargetType, projects: Project*): Project = project(t(), projects: _*)

  def project(t: Target,  projects: Project*): Project = ops.project(Target(moduleOps, t ), projects: _*)
}



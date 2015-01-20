package com.inthenow.sbt.scalajs


import sbt._

trait ModuleTargetOps extends TargetOps {

  override def getDefaultSettings(m: ModuleOps, t: Target): Seq[Def.Setting[_]] = m.module.defaultSettings

  override def getDefaultSharedSettings(mOps: ModuleOps, t: Target): Seq[Def.Setting[_]] = Seq()

  override def mkRootProject(t: Target, b: RootBuild, cpd: Project*) = RootBuildProjectOps(b).mkModuleProject(t, cpd: _*)

  override def mkSingleProject(t: Target, b: SingleBuild, cpd: Project*) = SingleBuildProjectOps(b).mkModuleProject(t, cpd: _*)

  override def mkSharedProject(t: Target, b: SharedBuild, cpd: Project*): Project = SharedBuildProjectOps(b).mkModuleProject(t, cpd: _*)

  override def mkCommonBaseProject(t: Target, b: CommonBaseBuild, cpd: Project*): Project = CommonBaseBuildProjectOps(b).mkModuleProject(t, cpd: _*)

  override def mkSymLinkedProject(t: Target, b: SymLinkedBuild, cpd: Project*): Project = SymLinkedBuildProjectOps(b).mkModuleProject(t, cpd: _*)

  override def mkSbtLinkedProject(t: Target, b: SbtLinkedBuild, cpd: Project*): Project = SbtLinkedBuildProjectOps(b).mkModuleProject(t, cpd: _*)
}

case object Module extends TargetType with ModuleTargetOps {
  def apply(id: String = "Module", name: String = "module", settings: Seq[Def.Setting[_]] = Seq(), sharedSettings: Seq[Def.Setting[_]] = Seq()): Target =
    Target(Module, id, name, settings, sharedSettings)
}


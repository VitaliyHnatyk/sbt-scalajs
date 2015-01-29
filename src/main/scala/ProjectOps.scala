package com.inthenow.sbt.scalajs

import sbt._
import sbt.Keys._

case class ProjectParams(id:String, base:File, settings: Seq[Setting[_]],
                         projects: Seq[Project],   buildInit:( () =>Seq[Setting[_]]) = ( () => Seq[Setting[_]]()))

case class ProjectOptions(hidden:Boolean ,  addProjects:Boolean, callPostTarget:Boolean, callBuildInit:Boolean =true, copyProject:Boolean = false)

class ProjectOps(val buildOps: BuildOps, val srcHidden:Boolean = false) {
  val moduleOps = buildOps.moduleOps
  val crossModule = moduleOps.crossModule

  def crossModuleParams(targetOps: TargetOps, settings: Seq[Setting[_]], p: Seq[Project] ):ProjectParams = {

    ProjectParams(
      id =    crossModule.id,
      base = moduleOps.getBase,
      settings = moduleOps.getDefaultSettings ++ settings,
      projects = p
    )
  }

  def crossModuleOptions = ProjectOptions(hidden= false ,  addProjects = false, copyProject=false, callPostTarget= true, callBuildInit= false)

  def targetProjectParams(targetOps: TargetOps, hidden: Boolean, settings: Seq[Setting[_]], p: Seq[Project] ):ProjectParams = {
    val target = targetOps.target
    ProjectParams(
      id = buildOps.getProjectId(crossModule.id, target.name),
      base = buildOps.getProjectBase(crossModule.id, target.name, hidden),
      settings = moduleOps.getDefaultSettings ++ targetOps.getSettings() ++ settings,
      projects = p
    )
  }

  def targetProjectOptions = ProjectOptions(hidden= false ,  addProjects = true, copyProject=false, callPostTarget= true, callBuildInit= true)

  def moduleNameSettings: Seq[Setting[_]] = Seq(name := { moduleOps.getModuleName() })

  def projectNameSettings(targetOps: TargetOps): Seq[Setting[_]] = Seq(name := { buildOps.getProjectName(crossModule.id, targetOps.target.name)  })

  def noNameSettings() : Seq[Setting[_]] = Seq()

}

trait ProjectType {
  def apply(b: BuildOps): ProjectOps
  def getBuildOps(m: CrossModuleOps,  buildType:BuildType )( implicit log: Logger): BuildOps
}

class StandardProjectOps(buildOps: BuildOps) extends ProjectOps(buildOps)

trait Standard  extends ProjectType{
  def apply(b: BuildOps): ProjectOps
}

case object Standard extends Standard {
  def apply(b: BuildOps): ProjectOps = new StandardProjectOps(b)
  def getBuildOps(m: CrossModuleOps, buildType:BuildType )( implicit log: Logger): BuildOps = buildType.getBuildOps(m, this)
}


class SharedProjectOps(buildOps: BuildOps) extends ProjectOps(buildOps)

trait Shared  extends ProjectType{
  def apply(b: BuildOps): ProjectOps
}

case object Shared extends Shared  {
  def apply(b: BuildOps): ProjectOps = new SharedProjectOps(b)
  def getBuildOps(m: CrossModuleOps, buildType:BuildType )( implicit log: Logger): BuildOps = buildType.getBuildOps(m, this)
}


class EmptyProjectOps(buildOps: BuildOps) extends ProjectOps(buildOps, srcHidden = true)

trait Empty  extends ProjectType{
  def apply(b: BuildOps): ProjectOps
}

case object Empty extends Empty  {
  def apply(b: BuildOps): ProjectOps = new EmptyProjectOps(b)
  def getBuildOps(m: CrossModuleOps, buildType:BuildType )( implicit log: Logger): BuildOps =buildType.getBuildOps(m, this)
}
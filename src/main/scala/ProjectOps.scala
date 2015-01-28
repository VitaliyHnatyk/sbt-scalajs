package com.inthenow.sbt.scalajs

import sbt._
import sbt.Keys._

case class ProjectParams(id:String, base:File,name:Seq[Setting[_]], settings: Seq[Setting[_]],
                         projects: Seq[Project],   buildInit:( () =>Seq[Setting[_]]) = ( () => Seq[Setting[_]]()))

case class ProjectOptions(hidden:Boolean ,  addProjects:Boolean, callPostTarget:Boolean, callBuildInit:Boolean =true, copyProject:Boolean = false)

class ProjectOps(val buildOps: BuildOps) {

  val moduleOps = buildOps.moduleOps
  val crossModule = moduleOps.crossModule



  implicit class ProjectOps(p: Project) {
    def addProjects(projects: Seq[Project]): Project = {
      val pr: Seq[ProjectReference] = projects.map(d => d.project)
      val cpd = projects.map(d => ClasspathDependency(d, Some("compile;test->test")))
      p.dependsOn(cpd: _*).aggregate(pr: _*)
    }
  }

  def mkModuleProject(targetOps: TargetOps, hidden: Boolean, settings: Seq[Setting[_]], p: Seq[Project] ): Project = {

    val settings = SbtScalajs.noRootSettings ++ Seq(name := {
      moduleOps.getModuleName()
    })
    mkBaseProject(targetOps, hidden, settings, p).addProjects(p)
  }

  def crossModuleParams(targetOps: TargetOps, hidden: Boolean, settings: Seq[Setting[_]], p: Seq[Project] ):ProjectParams = {

      ProjectParams(
      id =    crossModule.id,
    base = moduleOps.getBase,
    name = Seq(),
    settings = moduleOps.getDefaultSettings ++ settings,
      projects = Seq()
    )
  }

  def crossModuleOptions = ProjectOptions(hidden= false ,  addProjects = false, copyProject=false, callPostTarget= true, callBuildInit= false)

  def mkBaseProject(targetOps: TargetOps, hidden: Boolean, settings: Seq[Setting[_]], p: Seq[Project] ): Project = {
    Project(
      id = crossModule.id,
      base = moduleOps.getBase,
      settings = moduleOps.getDefaultSettings ++ settings
    )
  }

  def targetProjectParams(targetOps: TargetOps, hidden: Boolean, settings: Seq[Setting[_]], p: Seq[Project] ):ProjectParams = {
    val target = targetOps.target
    ProjectParams(
      id = buildOps.getProjectId(crossModule.id, target.name),
      base = buildOps.getProjectBase(crossModule.id, target.name, hidden),
      name = projectNameSettings(targetOps),
      settings = moduleOps.getDefaultSettings ++ targetOps.getSettings() ++ settings ++ Seq(name := {
        buildOps.getProjectName(crossModule.id, target.name)
      }),
      projects = p
    )
  }

  def targetProjectOptions = ProjectOptions(hidden= false ,  addProjects = true, copyProject=false, callPostTarget= true, callBuildInit= true)

  /*
  def mkTargetProject(targetOps: TargetOps, hidden: Boolean, settings: Seq[Setting[_]], p: Seq[Project] ): Project = {
    val target = targetOps.target
    Project(
      id = buildOps.getProjectId(crossModule.id, target.name),
      base = buildOps.getProjectBase(crossModule.id, target.name, hidden),
      settings = moduleOps.getDefaultSettings ++ targetOps.getSettings() ++ settings
        ++ Seq(name := {
        buildOps.getProjectName(crossModule.id, target.name)
      }))
  }
*/
  def moduleNameSettings: Seq[Setting[_]] = Seq(name := { moduleOps.getModuleName() })

  def projectNameSettings(targetOps: TargetOps): Seq[Setting[_]] = Seq(name := { buildOps.getProjectName(crossModule.id, targetOps.target.name)  })

  def noNameSettings() : Seq[Setting[_]] = Seq()

  //def mkSubProject(tp: Target, hidden: Boolean = false, settings: Seq[Setting[_]], p: Seq[Project]): Project = tp.mkProject(mkTargetProject(tp,hidden, Seq(), p).addProjects(p:_*))

}

trait ProjectType {
  def apply(b: BuildOps): ProjectOps
  def getBuildOps(m: CrossModuleOps,  buildType:BuildType ): BuildOps
}

trait Standard  extends ProjectType{
  def apply(b: BuildOps): ProjectOps
}

case object Standard extends Standard {
  def apply(b: BuildOps): ProjectOps = new ProjectOps(b)
  def getBuildOps(m: CrossModuleOps, buildType:BuildType ): BuildOps = buildType.getBuildOps(m, this)
}

class SharedProjectOps(buildOps: BuildOps) extends ProjectOps(buildOps)

  /*
  override def mkTargetProject(targetOps: TargetOps, hidden: Boolean, settings: Seq[Setting[_]], p: Seq[Project] ): Project = {
    val target = targetOps.target
    Project(
      id = buildOps.getProjectId(crossModule.id, target.name),
      base = buildOps.getProjectBase(crossModule.id, target.name, hidden),
      settings = settings ++ moduleOps.getDefaultSettings ++  targetOps.getSettings()
    )
  }
}
*/
trait Shared  extends ProjectType{
  def apply(b: BuildOps): ProjectOps
}

case object Shared extends Shared  {
  def apply(b: BuildOps): ProjectOps = new SharedProjectOps(b)
  def getBuildOps(m: CrossModuleOps, buildType:BuildType ): BuildOps = buildType.getBuildOps(m, this)
}
package com.inthenow.sbt.scalajs

import com.inthenow.sbt.scalajs.SbtScalajs._
import sbt._

case class Target(targetType: TargetType, id: String, name: String, settings: Seq[Def.Setting[_]]){

}

abstract class  TargetOps(val target: Target,val projectOps: ProjectOps) {
  implicit class ProjectOps(p: Project) {
    def addProjects(projects: Seq[Project]): Project = {
      val pr: Seq[ProjectReference] = projects.map(d => d.project)
      val cpd = projects.map(d => ClasspathDependency(d, Some("compile;test->test")))
      p.dependsOn(cpd: _*).aggregate(pr: _*)
    }
    def addSubProjects(projects: Seq[Project]): Project = {
      val deps = projects.foldLeft(Seq[ClasspathDep[ProjectReference]]())((s,p) => s ++ p.dependencies)
      p.copy( dependencies =  p.dependencies ++ deps)
    }
  }

  val targetType: TargetType = target.targetType
  val id: String = target.id
  val name: String = target.name
  val settings: Seq[Def.Setting[_]] = target.settings

  def getSettings(): Seq[Def.Setting[_]] = {
    getDefaultSettings() ++ settings
  }

  def getDefaultSettings(): Seq[Def.Setting[_]] = {
    scalajsTargetSettings(name) ++ CrossVersionSources()
  }

  def mkProject(p: Project): Project = p

  def mkProjectsXX(  projects: Seq[Project]): Project = {
    projectOps.buildOps.mkProject(this, projects)
  }

  def mkProject(b:BuildOps, params:ProjectParams, options:ProjectOptions): Project = {
    def buildInit = if(options.callBuildInit) params.buildInit() else Seq()
    def copyProject = if(options.copyProject) params.projects.foldLeft(Seq[Setting[_]]())((s,p) => s ++ p.settings) else Seq()

    val p = Project(
      id = params.id,
      base = params.base,
      settings = params.settings ++ buildInit ++ copyProject //).distinct
    )

    val result1 = if(options.addProjects) p.addProjects(params.projects) else p

    val result = if(options.copyProject)  result1.addSubProjects(params.projects) else result1

    if (options.callPostTarget) mkProject(result) else result
  }
}

trait TargetType {
  def apply(id: String = "",
            name: String = "",
            settings: Seq[Def.Setting[_]] = Seq()): Target

  def targetOps(target:Target, projectOps:ProjectOps):TargetOps
}


abstract class  SharedTargetOps(target: Target,projectOps: ProjectOps) extends TargetOps(target, projectOps) {
  override def getDefaultSettings(): Seq[Def.Setting[_]] = {
    super.getDefaultSettings() ++ CrossVersionSharedSources(projectOps.moduleOps.getSharedLabel)
  }
}

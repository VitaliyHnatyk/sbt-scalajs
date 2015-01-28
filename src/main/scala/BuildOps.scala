
package com.inthenow.sbt.scalajs

import sbt._

abstract class BuildOps(val moduleOps: CrossModuleOps, val buildName:String)   {
  def mkProject(target:TargetOps  , projects:Seq[Project]): Project

  implicit class ProjectOps(p: Project) {
    def addProjects(projects: Seq[Project]): Project = {
      val pr: Seq[ProjectReference] = projects.map(d => d.project)
      val cpd = projects.map(d => ClasspathDependency(d, Some("compile;test->test")))
      p.dependsOn(cpd: _*).aggregate(pr: _*)
    }
  }

  def project(t: Target, projectType:ProjectType, projects: Seq[Project]): Project = {

    val projectOps = projectType(this)
    val targetOps =  t.targetType.targetOps(t, projectOps)

    mkProject(targetOps,   projects)
  }

  def getProjectBase(projectId: String, projectDir: String, hidden: Boolean = false): File = {
    def mkDir(f:File) = {
      if (!f.exists()) {
        val d = new java.io.File(f.getCanonicalPath)
        d.mkdirs()
      }
    }

    // if the real base dir does not exist, make it and force a hidden one.
    val baseDir    = moduleOps.getBase / projectDir
    val hiddenDir   = moduleOps.getBase / s".$projectDir"
    val hasBaseDir = baseDir.exists()

    if (!hasBaseDir) {
      mkDir(hiddenDir / "src/main/scala")
      mkDir(hiddenDir / "src/test/scala")
    }
    if (hidden || !hasBaseDir) hiddenDir else baseDir
  }

  def getProjectId(projectId: String, projectDir: String) = s"${moduleOps.crossModule.id}_$projectDir"

  def getProjectName(projectId: String, projectDir: String) = s"${moduleOps.crossModule.modulePrefix}${moduleOps.crossModule.id}"
}

trait BuildType {
  def getBuildOps(m: CrossModuleOps, projectType:Standard): BuildOps
  def getBuildOps(m: CrossModuleOps, projectType:Shared ): BuildOps
}

abstract class SharedBuildOps( moduleOps: CrossModuleOps, buildName:String) extends BuildOps(moduleOps, buildName){

  override def getProjectBase(projectId: String, projectDir: String, hidden: Boolean ): File = {
    if(hidden) moduleOps.getBase / s".${moduleOps.getSharedLabel}_$projectDir"   else moduleOps.getBase / moduleOps.getSharedLabel
  }

  override def getProjectId(projectId: String, projectDir: String) = {
    s"${moduleOps.crossModule.id}_${moduleOps.getSharedLabel}_$projectDir"
  }

  override def getProjectName(projectId: String, projectDir: String) = {
    s"${moduleOps.crossModule.modulePrefix}${projectId}_${moduleOps.getSharedLabel}"
  }
}


trait SharedBuildType extends BuildType {

}



  // SingleBuild Std
  // calls mkBaseProject
  // -- name = project, target = target, hidden = false, settings= target.settings  ,   add subprojects = false , copy plugins = false , call target mkProject = true

  // RootBuild Std
  // for moduleOps:
  // calls mkModuleProject:
  // -- name = module. -- target = target, hidden = false, settings=None,   add subprojects = false , copy plugins = false , call target mkProject = false
  // otherwise calls sharecd


  // RootBuild Shared
  // overides the project base, id and name
  // - calls  mkTargetProject:
  // -- name = project. -- target = target, hidden = true, settings=None,   add subprojects = true , copy plugins = false , call target mkProject = true



   // sharedBuild - std
  // calls mkTargetProject:
  // -- name = project. -- target = target, hidden = false, settings=None,   add subprojects = true , copy plugins = false , call target mkProject = true
  // sharedBuild - shared
  // calls mkTargetProject:
  // -- name = project.-- target = target, hidden = true, settings=None,   add subprojects = false , copy plugins = false , call target mkProject = true


  // Common std = Sharedbuild std
  // Common shared = Sharedbuild shared + overides Project Base


    //SymLinked Std
      // - creates physical symbolic links to projects and adds them as cleanFiles to the target settings
      // - calls  mkTargetProject:
      // -- name = project -- target = target, hidden = true, settings= new settings,   add subprojects = true , copy plugins = true , call target mkProject = true


      //SbtLinked Std
      // - creates new linksources settings from projects and adds to target settings
      // - calls  mkTargetProject:
      // -- name = project -- target = target, hidden = true, settings= new settings,   add subprojects = true , copy plugins = true , call target mkProject = true

      //SymLinked and SbtLinked Shared
      // - calls  mkTargetProject:
      //-- name = None -- target = target, hidden = true, settings=  noRootSettings,    add subprojects = true , copy plugins = false , call target mkProject = false






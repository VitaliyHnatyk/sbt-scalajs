
package com.inthenow.sbt.scalajs

import sbt._

abstract class BuildOps(val moduleOps: CrossModuleOps, val buildName:String)( implicit log: Logger)   {
  def mkProject(target:TargetOps  , projects:Seq[Project]): Project

  def project(t: Target, projectType:ProjectType, projects: Seq[Project]): Project = {

    val projectOps = projectType(this)
    val targetOps =  t.targetType.targetOps(t, projectOps)

    mkProject(targetOps,   projects)
  }

  def getProjectBase(projectId: String, projectDir: String, hidden: Boolean = false): File = {
    if (hidden) moduleOps.getBase / s".$projectDir" else  moduleOps.getBase / projectDir

   /* def mkDir(f:File) = {
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
      log.info(s"Creating project directory ${hiddenDir.getCanonicalPath}")
      mkDir(hiddenDir / "src/main/scala")
      mkDir(hiddenDir / "src/test/scala")
    }
    if (hidden || !hasBaseDir) hiddenDir else baseDir
    */
  }

  def getProjectId(projectId: String, projectDir: String) = s"${moduleOps.crossModule.id}_$projectDir"

  def getProjectName(projectId: String, projectDir: String) = s"${moduleOps.crossModule.modulePrefix}${moduleOps.crossModule.id}"
}

trait BuildType {
  def getBuildOps(m: CrossModuleOps, projectType:Empty)( implicit log: Logger): BuildOps
  def getBuildOps(m: CrossModuleOps, projectType:Standard)( implicit log: Logger): BuildOps
  def getBuildOps(m: CrossModuleOps, projectType:Shared )( implicit log: Logger): BuildOps
}


abstract class SharedBuildOps( moduleOps: CrossModuleOps, buildName:String)( implicit log: Logger) extends BuildOps(moduleOps, buildName){

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


abstract class EmptyBuildOps( moduleOps: CrossModuleOps, buildName:String)( implicit log: Logger) extends BuildOps(moduleOps, buildName){

  override def getProjectBase(projectId: String, projectDir: String, hidden: Boolean = false): File = {
    def mkDir(f:File) = {
      if (!f.exists()) {
        log.info(s"Creating project directory ${f.getCanonicalPath}")
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
}

trait EmptyBuildType extends BuildType {

}





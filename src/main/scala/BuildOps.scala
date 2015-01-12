package com.inthenow.sbt.scalajs


import sbt._
import sbt.Keys._

sealed trait BuildType {
  def ops(m: Module): BuildOps
}

sealed trait BuildOps {
  def jvmProject(): Project

  def jsProject(): Project

}

object BuildOps {
  def setBuildType(module: String, build: BuildType): String = {
    val result = build match {
      case SingleBuild => "SingleBuild"
      case _ => throw new Error(s"Unknown build type ${build.toString} for ${module}.build")
    }
    System.setProperty(s"${module}.build", result)
    result
  }

  def getBuildType(module: String, default: BuildType): BuildType = {
    val result = Option(System.getProperty(s"${module}.build")) match {
      case Some("SingleBuild") => SingleBuild
      case None => default
      case opt: Option[String] => throw new Error(s"Unknown build type $opt for ${module}.build")
      case _ => default
    }
    result
  }
}

case class SingleBuild(m: Module) extends BuildOps {

  def jvmProject(): Project = mkProject(m.tp)

  def jsProject(): Project = mkProject(m.tp).enablePlugins(SbtScalajs)

  protected def mkProject(tp: Target): Project = {
    Project(
      id = m.id,
      base = m.base,
      settings = m.getDefaultSettings ++ tp.settings ++ Seq(name := {
        m.getProjectName(m.id, tp.name)
      })
    )
  }
}

case object SingleBuild extends BuildType {
  def ops(m: Module) = new SingleBuild(m)
}

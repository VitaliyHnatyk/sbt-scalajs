package com.inthenow.sbt.scalajs

import sbt._

class JsTargetOps(target:Target, projectOps: ProjectOps) extends TargetOps(target, projectOps)   {
  override def mkProject(p: Project): Project = p.enablePlugins(SbtScalajs)
}

case object Js extends TargetType {
  def apply(id: String = "Js", name: String = "js", settings: Seq[Def.Setting[_]] = Seq()): Target =
    Target(Js, id, name, settings)

  def targetOps(target:Target,projectOps: ProjectOps): TargetOps = new JsTargetOps(target, projectOps)
}

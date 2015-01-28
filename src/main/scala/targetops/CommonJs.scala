package com.inthenow.sbt.scalajs

import sbt._

class CommonJsOps(target:Target, projectOps: ProjectOps) extends JsTargetOps(target, projectOps)

case object CommonJs extends TargetType {
  def apply(id: String = "CommonJs", name: String = "commonjs", settings: Seq[Def.Setting[_]] = Seq()): Target =
    Target(CommonJs, id, name, settings)

  def targetOps(target:Target,projectOps: ProjectOps): TargetOps = new CommonJsOps(target, projectOps)
}


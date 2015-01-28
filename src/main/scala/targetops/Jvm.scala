package com.inthenow.sbt.scalajs

import sbt._


class JvmTargetOps(target:Target, projectOps: ProjectOps) extends TargetOps(target, projectOps)

case object Jvm extends TargetType {
  def apply(id: String = "Jvm", name: String = "jvm", settings: Seq[Def.Setting[_]] = Seq()): Target =
    Target(Jvm, id, name, settings)

  def targetOps(target:Target,projectOps: ProjectOps): TargetOps = new JvmTargetOps(target, projectOps)
}

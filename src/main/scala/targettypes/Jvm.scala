package com.inthenow.sbt.scalajs

import sbt._

trait JvmTargetOps extends TargetOps

case object Jvm extends TargetType with JvmTargetOps {
  def apply(id: String = "Jvm", name: String = "jvm", settings: Seq[Def.Setting[_]] = Seq(), sharedSettings: Seq[Def.Setting[_]] = Seq()): Target = {
    Target(Jvm, id, name, settings, sharedSettings)
  }

}

trait JvmSharedTargetOps extends CrossTargetOps

case object JvmShared extends TargetType with JvmSharedTargetOps {
  def apply(id: String = "Jvm", name: String = "jvm", settings: Seq[Def.Setting[_]] = Seq(), sharedSettings: Seq[Def.Setting[_]] = Seq()): Target = {
    Target(JvmShared, id, name, settings, sharedSettings)
  }
}





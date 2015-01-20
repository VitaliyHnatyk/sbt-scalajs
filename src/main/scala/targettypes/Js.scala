package com.inthenow.sbt.scalajs

import sbt._

trait JsTargetOps extends TargetOps {
  override def mkProject(p: Project): Project = p.enablePlugins(SbtScalajs)
}

case object Js extends TargetType with JsTargetOps {
  def apply(id: String = "Js", name: String = "js", settings: Seq[Def.Setting[_]] = Seq(), sharedSettings: Seq[Def.Setting[_]] = Seq()): Target =
    Target(Js, id, name, settings, sharedSettings)
}

trait JsSharedTargetOps extends CrossTargetOps  {
  override def mkSharedProject(t: Target, b: SharedBuild, cpd: Project*): Project = mkJsSharedProject(t, SharedBuildProjectOps(b), cpd: _*)

  def mkJsSharedProject(tp: Target, b: SharedBuildProjectOpsBase,  projects: Project*): Project =  {
    val shared =  projects.head
    b.mkSharedProject(tp, projects:_*).settings(SbtScalajs.linkedSources(shared): _*)
  }
}

case object JsShared extends TargetType with JsSharedTargetOps {
  def apply(id: String = "Js", name: String = "js", settings: Seq[Def.Setting[_]] = Seq(), sharedSettings: Seq[Def.Setting[_]] = Seq()): Target = {
    Target(JsShared, id, name, settings, sharedSettings)
  }

}

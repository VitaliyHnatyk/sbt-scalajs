package com.inthenow.sbt.scalajs

import sbt._

case object CommonJs extends TargetType with JsTargetOps {
  def apply(id: String = "CommonJs", name: String = "commonjs", settings: Seq[Def.Setting[_]] = Seq(), sharedSettings: Seq[Def.Setting[_]] = Seq()): Target =
    Target(Js, id, name, settings, sharedSettings)
}

case object CommonJsShared extends TargetType with JsSharedTargetOps {
  def apply(id: String = "CommonJs", name: String = "commonjs", settings: Seq[Def.Setting[_]] = Seq(), sharedSettings: Seq[Def.Setting[_]] = Seq()): Target = {
    Target(CommonJsShared, id, name, settings, sharedSettings)
  }
}
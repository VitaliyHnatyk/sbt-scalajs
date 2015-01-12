package com.inthenow.sbt.scalajs

trait Targets {
  val jvm: JvmTarget
  val js: JsTarget
  val commonJs: CommonJsTarget
}

trait TargetsType {
  val targets: Targets
}

case class XTargets(jvm: JvmTarget = new JvmTarget,
                    js: JsTarget = new JsTarget,
                    commonJs: CommonJsTarget = new CommonJsTarget()) extends Targets

object XTargets extends TargetsType {
  val targets: Targets = new XTargets
}


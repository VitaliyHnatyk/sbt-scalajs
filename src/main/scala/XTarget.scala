package com.inthenow.sbt.scalajs

import sbt._
import SbtScalajs._


trait Target{
  val name: String

  def setName(s: String):String

}

trait TargetsSig

trait STargetsSig extends TargetsSig

trait XTargetsSig extends TargetsSig {
  implicit val jvm: JvmTarget
  implicit val js: JsTarget
}

class XTargets extends XTargetsSig {
  implicit val jvm: JvmTarget = new JvmTarget
  implicit val js: JsTarget = new JsTarget
}

object XTargets {
  implicit def apply: XTargets = new XTargets
}

trait AllTargets {
  implicit val jvmTarget: JvmTarget
  implicit val jsTarget: JsTarget
  implicit val jsCommonTarget: CommonJsTarget
}

class XTarget(var id: String, var settings: Seq[Def.Setting[_]] = Seq()) extends Target {

  val name: String = setName(id)

  def setName(s: String):String = s.toLowerCase
}

class JsTarget(id: String = "JS", settings: Seq[Def.Setting[_]] = scalajsJsSettings) extends XTarget(id, settings)

object JsTarget {
  implicit val js: JsTarget = new JsTarget()
}

class CommonJsTarget(id: String = "CommonJS", settings: Seq[Def.Setting[_]] = scalajsCommonJsSettings) extends JsTarget(id, settings)

object CommonJsTarget {
  implicit val jvm: CommonJsTarget = new CommonJsTarget()
}

class JvmTarget(id: String = "JVM", settings: Seq[Def.Setting[_]] = scalajsJvmSettings) extends XTarget(id, settings)

object JvmTarget {
  implicit val jvm: JvmTarget = new JvmTarget()
}

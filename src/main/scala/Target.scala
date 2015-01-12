package com.inthenow.sbt.scalajs

import sbt._
import SbtScalajs._

trait Target{
  val settings: Seq[Def.Setting[_]]
  val id: String
  val name: String

  def setName(s: String):String
}

trait TargetType {
  val target:Target
}

abstract class TargetBase(val id: String, val settings: Seq[Def.Setting[_]] = Seq()) extends Target {

  val name: String = setName(id)

  def setName(s: String):String = s.toLowerCase
}

class JsTarget(id: String = "JS", settings: Seq[Def.Setting[_]] = scalajsJsSettings) extends TargetBase(id, settings)

object JsTarget  extends TargetType {
  val target:Target = new JsTarget()
}

class CommonJsTarget(id: String = "CommonJS", settings: Seq[Def.Setting[_]] = scalajsCommonJsSettings) extends JsTarget(id, settings)

object CommonJsTarget extends TargetType {
  val target:Target =  new CommonJsTarget()
}

class JvmTarget(id: String = "JVM", settings: Seq[Def.Setting[_]] = scalajsJvmSettings) extends TargetBase(id, settings)

object JvmTarget extends TargetType {
  val target:Target =   new JvmTarget()
}

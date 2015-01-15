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

class JsTarget(id: String = "Js", settings: Seq[Def.Setting[_]] = Seq()) extends TargetBase(id, settings)

object JsTarget  extends TargetType {
  val target:Target = new JsTarget()
}

class CommonJsTarget(id: String = "CommonJs", settings: Seq[Def.Setting[_]] = Seq()) extends JsTarget(id, settings)

object CommonJsTarget extends TargetType {
  val target:Target =  new CommonJsTarget()
}

class JvmTarget(id: String = "Jvm", settings: Seq[Def.Setting[_]] = Seq()) extends TargetBase(id, settings)

object JvmTarget extends TargetType {
  val target:Target =   new JvmTarget()
}

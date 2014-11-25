package com.inthenow.sbt.scalajs

import sbt._
import SbtScalajs._


class XTarget(var id: String, var settings: Seq[Def.Setting[_]] = Seq()) {

  val name: String = setName(id)

  def setName(s: String) = s.toLowerCase
}

class JsTarget(id: String = "JS", settings: Seq[Def.Setting[_]] = scalajsJsSettings) extends XTarget(id, settings)

object JsTarget {
  implicit val js: JsTarget = new JsTarget()
}

class CommonJsTarget(id: String = "CommonJS") extends JsTarget(id)

class JvmTarget(id: String = "JVM", settings: Seq[Def.Setting[_]] = scalajsJsSettings) extends XTarget(id, settings)

object JvmTarget {
  implicit val jvm: JvmTarget = new JvmTarget()
}

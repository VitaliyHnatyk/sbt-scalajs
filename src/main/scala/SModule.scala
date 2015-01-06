package com.inthenow.sbt.scalajs

import sbt._
import sbt.Keys._

case class SModule[T <: XTarget, B <: SingleBuildOps[T]](id: String,
                                                         baseDir: String = ".",
                                                         modulePrefix: String = "",
                                                         sharedLabel: String = "",
                                                         defaultSettings: Seq[Def.Setting[_]] = Seq())
                                                        (implicit val target: T, implicit val log: Logger)
  extends DefaultModuleOps {}


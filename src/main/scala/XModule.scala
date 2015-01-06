package com.inthenow.sbt.scalajs



import sbt._
import sbt.Keys._

case class XModule[T <: XTargetsSig, B <: BuildOps[T]](id: String,
                                                       baseDir: String = ".",
                                                       modulePrefix: String = "",
                                                       sharedLabel: String = "shared",
                                                       defaultSettings: Seq[Def.Setting[_]] = Seq())
                                                      (implicit val targets: T, implicit val log: Logger)
  extends DefaultModuleOps {

  def project(jvm: Project, js: Project): Project =
    Project(
      id = id,
      base = base,
      settings = SbtScalajs.noRootSettings ++ getDefaultSettings ++ Seq(name := {
        getModuleName()
      })
    ).dependsOn(jvm, js).aggregate(jvm, js)

}




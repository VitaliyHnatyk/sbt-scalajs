package com.inthenow.sbt.scalajs

import sbt._

class ProjectProxy {
  var aggregate: Seq[ProjectReference] = Nil
  var dependencies: Seq[ClasspathDep[ProjectReference]] = Nil
  var settings: Seq[Def.Setting[_]] = Nil
  var enablePlugins: Seq[Plugins] = Nil
  var disablePlugins: Seq[AutoPlugin] = Nil

  def aggregate(refs: ProjectReference*): ProjectProxy = {
    aggregate = aggregate ++ refs
    this
  }

  def dependsOn(deps: ClasspathDep[ProjectReference]*): ProjectProxy = {
    dependencies = dependencies ++ deps
    this
  }

  def settings(ss: Setting[_]*): ProjectProxy = {
    settings = settings ++ ss
    this
  }

  def enablePlugins(ns: Plugins*): ProjectProxy = {
    enablePlugins = enablePlugins ++ ns
    this
  }

  def disablePlugins(ps: AutoPlugin*): ProjectProxy = {
    disablePlugins = disablePlugins ++ ps
    this
  }
}
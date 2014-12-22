package com.inthenow.sbt.scalajs

import org.scalatest.{ Matchers, FunSpec }
import sbt.Keys._
import sbt.Project
import sbt._
import scala.scalajs.sbtplugin.ScalaJSPlugin._

class XModuleTest extends FunSpec with Matchers {
  implicit val logger:Logger = ConsoleLogger()

  val scalaz_js = Seq(libraryDependencies += "com.github.japgolly.fork.scalaz" %%% "scalaz-core" % "7.0.6")

  describe("An XModule") {

    it("Should create a new module with just an Id") {

      lazy val RDF = XModule(id = "rdf")

      RDF.id shouldBe "rdf"
      RDF.base.getName  shouldBe "."
      RDF.defaultSettings  shouldBe Seq()
    }

  }
  describe("A root XModule project") {
    import SharedProject._
    lazy val RDF = XModule(id = "rdf")
    lazy val rdf = RDF.project(rdf_jvm, rdf_js)

    lazy val rdf_jvm = RDF.jvmProject(rdf_common_jvm)

    lazy val rdf_common_jvm = RDF.jvmShared().settings(
      libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.0" % "test"
    )

    lazy val rdf_js = RDF.jsProject(rdf_common_js)
    lazy val rdf_common_js = RDF.jsShared(rdf_jvm).settings(scalaz_js :_*)

    it("should create a new project ") {

      rdf.id  shouldBe "rdf"
      rdf.base.getName shouldBe "."
    }

    it("should create a new jvm project ") {
      rdf_jvm.id shouldBe "rdf_jvm"
      rdf_jvm.base.getName shouldBe "jvm"

    }

    it("should create a new js project ") {
      rdf_js.id shouldBe "rdf_js"
      rdf_js.base.getName shouldBe "js"

    }
    it("should create a new jvm common project ") {
      rdf_common_jvm.id shouldBe "rdf_shared_jvm"
      rdf_common_jvm.base.getName shouldBe "shared"

    }

    it("should create a new js common project ") {
      rdf_common_js.id shouldBe "rdf_shared_js"
      rdf_common_js.base.getName shouldBe ".shared_js"

    }
  }

  describe("A non-root XModule project") {
    import SharedProject._
    lazy val RDF = XModule(id = "rdf", baseDir = "rdf", sharedLabel = "common")
    lazy val rdf = RDF.project(rdf_jvm, rdf_js)

    lazy val rdf_jvm = RDF.jvmProject(rdf_common_jvm)

    lazy val rdf_common_jvm = RDF.jvmShared().settings(
      libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.0" % "test"
    )

    lazy val rdf_js = RDF.jsProject(rdf_common_js)
    lazy val rdf_common_js = RDF.jsShared(rdf_jvm).settings(scalaz_js :_*)

    it("should create a new project ") {

      rdf.id  shouldBe "rdf"
      rdf.base.getName shouldBe "rdf"
    }

    it("should create a new jvm project ") {
      rdf_jvm.id shouldBe "rdf_jvm"
      rdf_jvm.base.getPath shouldBe "rdf/jvm"

    }

    it("should create a new js project ") {
      rdf_js.id shouldBe "rdf_js"
      rdf_js.base.getPath shouldBe "rdf/js"

    }
    it("should create a new jvm common project ") {
      rdf_common_jvm.id shouldBe "rdf_common_jvm"
      rdf_common_jvm.base.getPath shouldBe "rdf/common"

    }

    it("should create a new js common project ") {
      rdf_common_js.id shouldBe "rdf_common_js"
      rdf_common_js.base.getPath shouldBe "rdf/.common_js"

    }
  }

  describe("A non-root XModule project with custom target") {
    import SharedProject._

    implicit val js:JsTarget = new CommonJsTarget()
    implicit val jvm:JvmTarget = new JvmTarget("ibmJVM")
    lazy val RDF = XModule(id = "rdf", baseDir = "rdf", sharedLabel = "common")
    lazy val rdf = RDF.project(rdf_jvm, rdf_js)

    lazy val rdf_jvm = RDF.jvmProject(rdf_common_jvm)

    lazy val rdf_common_jvm = RDF.jvmShared().settings(
      libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.0" % "test"
    )

    lazy val rdf_js = RDF.jsProject(rdf_common_js)
    lazy val rdf_common_js = RDF.jsShared(rdf_jvm).settings(scalaz_js :_*)

    it("should create a new project ") {

      rdf.id  shouldBe "rdf"
      rdf.base.getName shouldBe "rdf"
    }

    it("should create a new jvm project ") {
      rdf_jvm.id shouldBe "rdf_ibmjvm"
      rdf_jvm.base.getPath shouldBe "rdf/ibmjvm"

    }

    it("should create a new js project ") {
      rdf_js.id shouldBe "rdf_commonjs"
      rdf_js.base.getPath shouldBe "rdf/commonjs"

    }
    it("should create a new jvm common project ") {
      rdf_common_jvm.id shouldBe "rdf_common_ibmjvm"
      rdf_common_jvm.base.getPath shouldBe "rdf/common"

    }

    it("should create a new js common project ") {
      rdf_common_js.id shouldBe "rdf_common_commonjs"
      rdf_common_js.base.getPath shouldBe "rdf/.common_commonjs"

    }
  }
  describe("A real shared module") {
    import LinkedProject._
    val module = XModule(id = "notests", modulePrefix = "banana-")

    lazy val rdf       = module.project(prjJvm, prjJs)
    lazy val prjJvm    = module.jvmProject()
    lazy val prjJs     = module.jsProject()
  }
}
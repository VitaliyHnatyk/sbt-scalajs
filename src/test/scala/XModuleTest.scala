package com.inthenow.sbt.scalajs

import org.scalatest.{Matchers, FunSpec}
import sbt.Keys._
import sbt.Project
import sbt._
import scala.scalajs.sbtplugin.ScalaJSPlugin._

class XModuleTest extends FunSpec with Matchers {
  implicit val logger: Logger = ConsoleLogger()

  val scalaz_js = Seq(libraryDependencies += "com.github.japgolly.fork.scalaz" %%% "scalaz-core" % "7.0.6")

  describe("An XModule") {

    it("Should create a new module with just an Id") {

      lazy val RDF = CrossModule(id = "rdf", build = SharedBuild) //, config = XShared)

      RDF.id shouldBe "rdf"
      RDF.base.getName shouldBe "."
      RDF.defaultSettings shouldBe Seq()
    }

  }
  describe("A root XModule project") {

    val linked = false
    val rdfBuild = if (linked) SymLinkedBuild else SharedBuild

    lazy val RDF            = CrossModule(id = "rdf", build = rdfBuild)
    lazy val rdf            = RDF.project(rdf_jvm, rdf_js)
    lazy val rdf_jvm        = RDF.jvmProject(rdf_common_jvm)
    lazy val rdf_common_jvm = RDF.jvmShared().settings(
      libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.0" % "test"
    )

    lazy val rdf_js = RDF.jsProject(rdf_common_js)
    lazy val rdf_common_js = RDF.jsShared(rdf_common_jvm).settings(scalaz_js: _*)

    it("should create a new project ") {

      rdf.id shouldBe "rdf"
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
      if (linked) {
        rdf_common_jvm.id shouldBe ""
        rdf_common_jvm.base.getName shouldBe ""
      }
      else {
        rdf_common_jvm.id shouldBe "rdf_shared_jvm"
        rdf_common_jvm.base.getName shouldBe "shared"
      }
    }

    it("should create a new js common project ") {
      if (linked) {
        rdf_common_js.id shouldBe ""
        rdf_common_js.base.getName shouldBe ""
      }
      else {
        rdf_common_js.id shouldBe "rdf_shared_js"
        rdf_common_js.base.getName shouldBe ".shared_js"
      }
    }
  }

  describe("A non-root XModule project") {

    val build = SharedBuild
    lazy val RDF = CrossModule(id = "rdf", build = build, baseDir = "rdf", sharedLabel = "common")
    lazy val rdf = RDF.project(rdf_jvm, rdf_js)

    lazy val rdf_jvm = RDF.jvmProject(rdf_common_jvm)

    lazy val rdf_common_jvm = RDF.jvmShared().settings(
      libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.0" % "test"
    )

    lazy val rdf_js = RDF.jsProject(rdf_common_js)
    lazy val rdf_common_js = RDF.jsShared(rdf_common_jvm).settings(scalaz_js: _*)

    it("should create a new project ") {

      rdf.id shouldBe "rdf"
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

    object IbmTargets extends TargetsType{
      val targets:Targets = new XTargets(jvm = new JvmTarget("ibmJVM"), js = new CommonJsTarget())
    }

    lazy val RDF = CrossModule(id = "rdf", build = SharedBuild, target = IbmTargets, baseDir = "rdf", sharedLabel = "common")
    lazy val rdf = RDF.project(rdf_jvm, rdf_js)

    lazy val rdf_jvm = RDF.jvmProject(rdf_common_jvm)

    lazy val rdf_common_jvm = RDF.jvmShared().settings(
      libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.0" % "test"
    )

    lazy val rdf_js = RDF.jsProject(rdf_common_js)
    lazy val rdf_common_js = RDF.jsShared(rdf_common_jvm).settings(scalaz_js: _*)

    it("should create a new project ") {

      rdf.id shouldBe "rdf"
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

    val build = SymLinkedBuild
    val module = CrossModule(id = "notests", build = build, modulePrefix = "banana-")

    lazy val rdf = module.project(prjJvm, prjJs)
    lazy val prjJvm = module.jvmProject(sharedjvm)
    lazy val prjJs = module.jsProject(sharedjs)
    lazy val sharedjvm = module.jvmShared()
    lazy val sharedjs = module.jsShared(sharedjvm)
  }
  describe("A single JVM module") {

    //type X = SBuildJvm
    val module = Module (id = "notests", build = SingleBuild, target = JvmTarget, modulePrefix = "banana-")

    lazy val rdf = module.jvmProject
  }

  describe("A single JS module") {

   // type X = SBuildJs
    //val module = SModule[X#Targets, X#BuildOps](id = "notests", modulePrefix = "banana-")
   val module = Module (id = "notests", build = SingleBuild, target = JsTarget, modulePrefix = "banana-")

    lazy val rdf = module.jsProject
  }
  describe("A single custom JS module") {

    object AndroidTarget  extends TargetType {
      val target:Target = new JsTarget(id ="androidJS")
    }

    val module = Module (id = "notests", build = SingleBuild, target = AndroidTarget, modulePrefix = "banana-")

    lazy val rdf = module.jsProject
  }
  describe("A single CommonJS module") {

    //type X = SBuildCommonJs
    //val module = SModule[X#Targets, X#BuildOps](id = "notests", modulePrefix = "banana-")
    val module = Module (id = "notests",  build = SingleBuild,target = CommonJsTarget, modulePrefix = "banana-")

    lazy val rdf: Project = module.jsProject

  }
  describe("A root module") {

    lazy val rootModule = CrossRootModule(moduleName = "MyModule")
    lazy val root = rootModule.project(rootJvm, rootJs)
    lazy val rootJvm = rootModule.jvmProject(prjJvm, prjJvm2, jena)
    lazy val rootJs = rootModule.jsProject(prjJs, prjJs2)

    lazy val build = SymLinkedBuild
    lazy val module = CrossModule(id = "notests", build = build, modulePrefix = "banana-")

    lazy val rdf = module.project(prjJvm, prjJs)
    lazy val prjJvm = module.jvmProject(sharedjvm)
    lazy val prjJs = module.jsProject(sharedjs)
    lazy val sharedjvm = module.jvmShared()
    lazy val sharedjs = module.jsShared(sharedjvm)

    lazy val module2 = CrossModule(id = "notests", build = build, modulePrefix = "banana-")

    lazy val rdf2 = module.project(prjJvm2, prjJs2)
    lazy val prjJvm2 = module.jvmProject(sharedjvm2)
    lazy val prjJs2 = module.jsProject(sharedjs2)
    lazy val sharedjvm2 = module.jvmShared()
    lazy val sharedjs2 = module.jsShared(sharedjvm2)

//    type XJ = SBuildJvm
 //   lazy val jenaModule = SModule[XJ#Targets, XJ#BuildOps](id = "notests", modulePrefix = "banana-")
    lazy val jenaModule = Module(id = "notests", build = SingleBuild,target = JvmTarget,  modulePrefix = "banana-")
    lazy val jena = jenaModule.jvmProject
  }
}
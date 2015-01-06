package com.inthenow.sbt.scalajs

import org.scalatest.{ Matchers, FunSpec }
import sbt.Keys._
import sbt.Project
import sbt._
import scala.scalajs.sbtplugin.ScalaJSPlugin._

class XModuleTest extends FunSpec with Matchers {
  implicit val logger:Logger = ConsoleLogger()

 // implicit val module  = new ModuleSig[SharedProject.type] {  type Project = SharedProject.SharedProjectOps[Targets]

  //  type Targets = XTargets}
  val scalaz_js = Seq(libraryDependencies += "com.github.japgolly.fork.scalaz" %%% "scalaz-core" % "7.0.6")

  describe("An XModule") {
      val sig = new XSharedBuild
    it("Should create a new module with just an Id") {

      lazy val RDF = XModule[sig.Targets, sig.BuildOps](id = "rdf")

      RDF.id shouldBe "rdf"
      RDF.base.getName  shouldBe "."
      RDF.defaultSettings  shouldBe Seq()
    }

  }
  describe("A root XModule project") {
    //import SharedProject._
    lazy val RDF = XModule[ XSharedBuild#Targets, XSharedBuild#BuildOps](id = "rdf")
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
   // import SharedProject._
    lazy val RDF = XModule[XSharedBuild#Targets, XSharedBuild#BuildOps](id = "rdf", baseDir = "rdf", sharedLabel = "common")
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

    class MyXTargets  extends XTargetsSig {
      implicit val js: JsTarget = new CommonJsTarget()
      implicit val jvm: JvmTarget = new JvmTarget("ibmJVM")
    }
    object MyXTargets {
      implicit def apply = new MyXTargets
    }
    trait MyX extends BuildSig {

      type Project = XSharedBuild.XSharedBuildOps[Targets]

      type Targets = MyXTargets

    }


    lazy val RDF = XModule[MyX#Targets, MyX#Project](id = "rdf", baseDir = "rdf", sharedLabel = "common")
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
    //import LinkedProject._

    type X = XLinkedBuild
    val module = XModule[X#Targets, X#BuildOps](id = "notests", modulePrefix = "banana-")

    lazy val rdf       = module.project(prjJvm, prjJs)
    lazy val prjJvm    = module.jvmProject(sharedjvm)
    lazy val prjJs     = module.jsProject(sharedjs)
    lazy val sharedjvm = module.jvmShared()
    lazy val sharedjs  = module.jsShared(sharedjvm)
  }
  describe("A single JVM module") {

    type X = SBuildJvm
    val module = SModule[X#Targets, X#BuildOps](id = "notests", modulePrefix = "banana-")

    lazy val rdf = module.jvmProject[X#Targets]
  }



  describe("A single JS module") {

    type X = SBuildJs
    val module = SModule[X#Targets, X#BuildOps](id = "notests", modulePrefix = "banana-")

    lazy val rdf = module.jsProject[X#Targets]
  }
  describe("A single CommonJS module") {

    type X = SBuildCommonJs
    val module = SModule[X#Targets, X#BuildOps](id = "notests", modulePrefix = "banana-")

    lazy val rdf:Project = module.jsProject[X#Targets]

  }
  describe("A root module") {

    lazy val rootModule = XRootModule(moduleName ="MyModule")
    lazy val root       = rootModule.project(rootJvm, rootJs)
    lazy val rootJvm    = rootModule.jvmProject(prjJvm, prjJvm2, jena)
    lazy val rootJs     = rootModule.jsProject(prjJs, prjJs2)

    type X = XLinkedBuild
    lazy val module = XModule[X#Targets, X#BuildOps](id = "notests", modulePrefix = "banana-")

    lazy val rdf       = module.project(prjJvm, prjJs)
    lazy val prjJvm    = module.jvmProject(sharedjvm)
    lazy val prjJs     = module.jsProject(sharedjs)
    lazy val sharedjvm = module.jvmShared()
    lazy val sharedjs  = module.jsShared(sharedjvm)

    lazy val module2 = XModule[X#Targets, X#BuildOps](id = "notests", modulePrefix = "banana-")

    lazy val rdf2       = module.project(prjJvm2, prjJs2)
    lazy val prjJvm2    = module.jvmProject(sharedjvm2)
    lazy val prjJs2     = module.jsProject(sharedjs2)
    lazy val sharedjvm2 = module.jvmShared()
    lazy val sharedjs2  = module.jsShared(sharedjvm2)

    type XJ = SBuildJvm
    lazy val jenaModule = SModule[XJ#Targets, XJ#BuildOps](id = "notests", modulePrefix = "banana-")

    lazy val jena = jenaModule.jvmProject[XJ#Targets]
  }
}
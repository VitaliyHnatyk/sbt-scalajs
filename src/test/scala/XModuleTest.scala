package com.inthenow.sbt.scalajs

import org.scalatest.{Matchers, FunSpec}
import sbt.Keys._
import sbt._
import org.scalajs.sbtplugin._
import ScalaJSPlugin.autoImport._

class XModuleTest extends FunSpec with Matchers {
  implicit val logger: Logger = ConsoleLogger()

  val scalaz_js = Seq(libraryDependencies += "com.github.japgolly.fork.scalaz" %%% "scalaz-core" % "7.0.6")


  describe("An CrossRootModule") {
    it("Should create a new root module with just an Id") {


      val RDF = CrossModule(RootBuild, id = "rdf")

      RDF.id shouldBe "rdf"
      RDF.moduleOps.getBase.getName shouldBe "."
      RDF.defaultSettings shouldBe Seq()
    }

  }
  describe("An XModule") {
    it("Should create a new module with just an Id") {

        val RDF = CrossModule(SharedBuild, id = "rdf" ) //, config = XShared)

      RDF.id shouldBe "rdf"
      RDF.moduleOps.getBase.getName shouldBe "."
      RDF.defaultSettings shouldBe Seq()
    }

  }

  describe("A cross module root project") {
    val linked = false
    val rdfBuild = if (linked) SymLinkedBuild else SharedBuild

    lazy val RDF            = CrossModule(rdfBuild, id = "rdf" )
    lazy val rdf            = RDF.project(Module, rdf_jvm, rdf_js)
    lazy val rdf_jvm        = RDF.project(Jvm, rdf_common_jvm)
    lazy val rdf_common_jvm = RDF.project(Jvm, Shared).settings(
      libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.0" % "test"
    )

    lazy val rdf_js = RDF.project(Js, rdf_common_js)
    lazy val rdf_common_js = RDF.project(Js,Shared, rdf_common_jvm).settings(scalaz_js: _*)

    it("should create a new module ") {

      RDF.id shouldBe "rdf"
      RDF.moduleOps.getBase.getName shouldBe "."
    }

    it("should create a new module project ") {

      rdf.id shouldBe "rdf"
      rdf.base.getName shouldBe "."
    }

    it("should create a new jvm project ") {
      rdf_jvm.id shouldBe "rdf_jvm"
      rdf_jvm.base.getName shouldBe ".jvm"

    }

    it("should create a new js project ") {
      rdf_js.id shouldBe "rdf_js"
      rdf_js.base.getName shouldBe ".js"

    }
    it("should create a new jvm common project ") {
      if (linked) {
        rdf_common_jvm.id shouldBe "rdf_shared_jvm"
        rdf_common_jvm.base.getName shouldBe ".shared_jvm"
      }
      else {
        rdf_common_jvm.id shouldBe "rdf_shared_jvm"
        rdf_common_jvm.base.getName shouldBe ".shared_jvm"
      }
    }

    it("should create a new js common project ") {
      if (linked) {
        rdf_common_js.id shouldBe "rdf_shared_js"
        rdf_common_js.base.getName shouldBe ".shared_js"
      }
      else {
        rdf_common_js.id shouldBe "rdf_shared_js"
        rdf_common_js.base.getName shouldBe ".shared_js"
      }
    }
  }

    describe("A non-root XModule project") {

      val build = SharedBuild
      lazy val RDF = CrossModule(build, id = "rdf",  baseDir = "rdf", sharedLabel = "common")
      lazy val rdf = RDF.project(Module, rdf_jvm, rdf_js)

      lazy val rdf_jvm = RDF.project(Jvm, rdf_common_jvm)

      lazy val rdf_common_jvm = RDF.project(Jvm,Shared).settings(
        libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.0" % "test"
      )

      lazy val rdf_js = RDF.project(Js, rdf_common_js)
      lazy val rdf_common_js = RDF.project(Js,Shared).settings(scalaz_js: _*)

      it("should create a new project ") {

        rdf .id shouldBe "rdf"
        rdf .base.getName shouldBe "rdf"
      }

      it("should create a new jvm project ") {
        rdf_jvm.id shouldBe "rdf_jvm"
        rdf_jvm.base.getPath shouldBe "rdf/.jvm"

      }

      it("should create a new js project ") {
        rdf_js.id shouldBe "rdf_js"
        rdf_js.base.getPath shouldBe "rdf/.js"

      }
      it("should create a new jvm common project ") {
        rdf_common_jvm.id shouldBe "rdf_common_jvm"
        rdf_common_jvm.base.getPath shouldBe "rdf/.common_jvm"

      }

      it("should create a new js common project ") {
        rdf_common_js.id shouldBe "rdf_common_js"
        rdf_common_js.base.getPath shouldBe "rdf/.common_js"

      }
    }

    describe("A non-root XModule project with custom target") {

      val ibmJVM = Jvm(id = "ibmJVM", name ="ibmjvm")
      val js  = CommonJs  (id="js", name ="js")


      lazy val RDF = CrossModule(SharedBuild, id = "rdf",  baseDir = "rdf", sharedLabel = "common")

      lazy val rdf = RDF.project(Module, rdf_jvm, rdf_js)
      lazy val rdf_jvm = RDF.project(ibmJVM, rdf_common_jvm)
      lazy val rdf_common_jvm = RDF.project(ibmJVM, Shared).settings(
        libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.0" % "test"
      )

      lazy val rdf_js = RDF.project(js, rdf_common_js)
      lazy val rdf_common_js = RDF.project(js, Shared).settings(scalaz_js: _*)

      it("should create a new project ") {

        rdf.id shouldBe "rdf"
        rdf.base.getName shouldBe "rdf"
      }

      it("should create a new jvm project ") {
        rdf_jvm.id shouldBe "rdf_ibmjvm"
        rdf_jvm.base.getPath shouldBe "rdf/.ibmjvm"

      }

      it("should create a new js project ") {
        rdf_js.id shouldBe "rdf_js"
        rdf_js.base.getPath shouldBe "rdf/.js"

      }
      it("should create a new jvm common project ") {
        rdf_common_jvm.id shouldBe "rdf_common_ibmjvm"
        rdf_common_jvm.base.getPath shouldBe "rdf/.common_ibmjvm"

      }

      it("should create a new js common project ") {
        rdf_common_js.id shouldBe "rdf_common_js"
        rdf_common_js.base.getPath shouldBe "rdf/.common_js"

      }
    }

    describe("A real shared module") {
      val build = SymLinkedBuild
      val module = CrossModule( build, id = "notests",  modulePrefix = "banana-")

      lazy val rdf = module.project(Module, prjJvm, prjJs)
      lazy val prjJvm = module.project(Jvm, sharedjvm)
      lazy val prjJs = module.project(Js, sharedjs)
      lazy val sharedjvm = module.project(Jvm,Shared)
      lazy val sharedjs = module.project(Js, Shared)
    }

  describe("A single JVM module") {
    val module = CrossModule (SingleBuild, id = "notests", modulePrefix = "banana-")

    lazy val rdf = module.project(Jvm)
  }

  describe("A single JS module") {
    val module = CrossModule (SingleBuild, id = "notests", modulePrefix = "banana-")

    lazy val rdf = module.project(Js)
  }
/*
  describe("A single custom JS module") {
    object AndroidTarget  extends TargetType {
      def apply( id:String = "Js", name:String  = "js", settings: Seq[Def.Setting[_]] = Seq(), sharedSettings: Seq[Def.Setting[_]] =Seq(),  targets:Targets = Targets()):Target =
        Target(AndroidTarget, id, name,  settings, sharedSettings)

      def mkProject(p:Project):Project = Js.mkProject(p)

      def getDefaultSettings(m: ModuleOps) =  Js.getDefaultSettings(m)
      def getDefaultSharedSettings(m: ModuleOps): Seq[Def.Setting[_]] = Js.getDefaultSharedSettings(m)
    }

    val module = Module (id = "notests", build = SingleBuild, targetType = AndroidTarget, modulePrefix = "banana-")
    lazy val rdf = module.project
  }

  describe("A single CommonJS module") {
    val module = Module (id = "notests",  build = SingleBuild,targetType = CommonJs, modulePrefix = "banana-")
    lazy val rdf: Project = module.project
  }
*/
  describe("A root module") {

    val rootModule = CrossModule(RootBuild, id = "MyModule")

    //lazy val rootJvm = rootModule.jvmProject(prjJvm, prjJvm2, jena)
    //lazy val rootJs = rootModule.jsProject(prjJs, prjJs2)

    lazy val rootJvm = rootModule.project(Jvm, jena) //prjJvm, prjJvm2, jena
   /////

/*

lazy val rootJs  = rootModule.project(Js, prjJs, prjJs2)
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
*/

    lazy val jenaModule = CrossModule(SingleBuild, id = "notests",  modulePrefix = "banana-")
    lazy val jena = jenaModule.project(Jvm)

    val root = rootModule.project(Module, rootJvm) //, rootJs)
  }


}



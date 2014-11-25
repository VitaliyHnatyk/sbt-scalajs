package com.inthenow.sbt.scalajs

import org.scalatest.{ Matchers, FunSpec }
import sbt.Keys._
import sbt._
import scala.scalajs.sbtplugin.ScalaJSPlugin._

class XTargetTest extends FunSpec with Matchers {

  describe("A js target") {
    it("should work with no arguments") {
      val m = new JsTarget()

      m.id shouldBe "JS"
      m.name shouldBe "js"
      m.settings shouldNot  equal(Seq())
    }
    it("Should work with just an ID") {
      val m =  new JsTarget("RDF")

      m.id shouldBe "RDF"
      m.name shouldBe "rdf"
      m.settings shouldNot  equal(Seq())
    }
    it("Should work with just settings") {
      val m =  new JsTarget(settings = Seq())

      m.id shouldBe "JS"
      m.name shouldBe "js"
      m.settings should  equal(Seq())
    }
  }
}

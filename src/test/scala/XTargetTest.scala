package com.inthenow.sbt.scalajs

import org.scalatest.{ Matchers, FunSpec }
import sbt._

class XTargetTest extends FunSpec with Matchers {
  implicit val logger: Logger = ConsoleLogger()
  describe("A js target") {
    it("should work with no arguments") {
      val m = Js()

      m.id shouldBe "Js"
      m.name shouldBe "js"
      m.settings should  equal(Seq())
    }
    it("Should work with just an ID") {
      val m =  Js("RDF", "rdf")

      m.id shouldBe "RDF"
      m.name shouldBe "rdf"
      m.settings should equal(Seq())
    }
    it("Should work with just settings") {
      val m =  Js(settings = Seq())

      m.id shouldBe "Js"
      m.name shouldBe "js"
      m.settings should  equal(Seq())
    }
  }
}

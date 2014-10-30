package test

/*
import org.specs2.mutable._

import play.api.test._
import play.api.test.Helpers._
import play.api.http.ContentTypes.JSON
*/


import org.scalatest._
import play.api.test._
import play.api.test.Helpers._
import org.scalatestplus.play._

class IntegrationTest extends PlaySpec with OneServerPerSuite with OneBrowserPerSuite with FirefoxFactory {

  /**
   * This integration test uses Play support for Selenium
   * to test the app with a browser
   */
  "The OneBrowserPerTest trait" must {
    "provide a web driver" in {
      //"run in browser" in new WithBrowser(webDriver = Helpers.FIREFOX) {
      go to (s"http://localhost:$port/")
      pageSource must include ("A Scala to JavaScript compiler")
    }
  }
}

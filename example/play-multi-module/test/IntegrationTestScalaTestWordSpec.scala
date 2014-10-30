package test


import org.scalatest.WordSpec
import org.scalatest.selenium._
import org.scalatest.Matchers
import play.api.test._
import play.api.test.Helpers._
import play.api.http.ContentTypes.JSON
import javax.swing.text.html.HTML
import com.sun.org.apache.xalan.internal.xsltc.compiler.Include


class IntegrationTestScalaTestWordSpec extends WordSpec with Matchers with Chrome {

  /**
   * This integration test uses ScalaTest with the WordSpec style and ScalaTest's
   * DSL for Selenium to test the app with a browser
   */
  val port = 3334
  val host = s"http://localhost:$port/"

   "run in browser" in { running (TestServer(port)) { 
      go to host
      pageSource should include ("A Scala to JavaScript compiler")
      close
    }
  }
}
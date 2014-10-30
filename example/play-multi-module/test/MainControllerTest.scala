
import play.api._
import play.api.mvc._
import play.api.test._
import play.api.test.Helpers._
import org.scalatest.FunSuite

/**
 *
 * ScalaTest using the FunSuite style and Play's FakeApplication
 *
 */
class MainControllerTest extends FunSuite {

  /*
  test("index template should contain the string that is passed to it") {
    running(FakeApplication()) {

      val html = views.html.testJson.render("Your new application is ready.")

      assert(contentType(html) === "text/html")
      assert(contentAsString(html).contains("Your new application is ready."))
    }
  }
*/
  test("index should contain the correct string") {
    running(FakeApplication()) {
      val result = controllers.MainController.index()(FakeRequest())

      assert(status(result)      === OK);
      assert(contentType(result) === (Some("text/html")))
      assert(charset(result)     === (Some("utf-8")))
      
      assert(contentAsString(result).contains("A Scala to JavaScript compiler"))
    }
  }

}

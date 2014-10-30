package controllers

import play.api.mvc._
import play.api.Routes


object MainController extends Controller {

  /**
   * The index page.  This is the main entry point
   */
  def index= Action {
    Ok(views.html.index.render("Hello from Java"))
  }
 
  /** The javascript router. */
  def router = Action { implicit req =>
    Ok(
      Routes.javascriptRouter("routes")(
        routes.javascript.MainController.index
      //  routes.javascript.MainController.testJson
        //,
        //routes.javascript.MessageController.getMessages,
        //routes.javascript.MessageController.saveMessage
      )
    ).as("text/javascript")
  }
}

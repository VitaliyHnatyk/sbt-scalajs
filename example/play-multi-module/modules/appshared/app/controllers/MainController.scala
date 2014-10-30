package controllers.appshared

import play.api.mvc._
import play.api.Routes



object MainController extends Controller {

  /** The javascript router. */
  def router = Action { implicit req =>
    Ok(
      Routes.javascriptRouter("routes")(
      //  routes.javascript.MainController.index
      //  routes.javascript.MainController.testJson
        //,
        //routes.javascript.MessageController.getMessages,
        //routes.javascript.MessageController.saveMessage
      )
    ).as("text/javascript")
  }
}



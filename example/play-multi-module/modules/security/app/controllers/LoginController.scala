package controllers.security

import play.api._
import play.api.mvc._
import org.pac4j.http.client._
import org.pac4j.core.profile._
import org.pac4j.play._
import org.pac4j.play.scala._
import play.api.libs.json.Json

object LoginController extends ScalaController {

  def login = Action { request =>
    val newSession = getOrCreateSessionId(request)
    val urlFacebook = getRedirectAction(request, newSession, "FacebookClient", "/?0").getLocation()
    val urlTwitter = getRedirectAction(request, newSession, "TwitterClient", "/?1").getLocation()
    val urlForm = getRedirectAction(request, newSession, "FormClient", "/?2").getLocation()
    val urlBA = getRedirectAction(request, newSession, "BasicAuthClient", "/?3").getLocation()
    val urlCas = getRedirectAction(request, newSession, "CasClient", "/?4").getLocation()
    val urlGoogleOpenId = getRedirectAction(request, newSession, "GoogleOpenIdClient", "/?5").getLocation()
    val profile = getUserProfile(request)
    Ok(views.html.security.login(profile, urlFacebook, urlTwitter, urlForm, urlBA, urlCas, urlGoogleOpenId)).withSession(newSession)
  }

  def facebookIndex = RequiresAuthentication("FacebookClient") { profile =>
    Action { request =>
      Ok(views.html.security.protectedIndex(profile))
    }
  }

  def twitterIndex = RequiresAuthentication("TwitterClient") { profile =>
    Action { request =>
      Ok(views.html.security.protectedIndex(profile))
    }
  }

  def formIndex = RequiresAuthentication("FormClient") { profile =>
    Action { request =>
      Ok(views.html.security.protectedIndex(profile))
    }
  }

  // Setting the isAjax parameter to true will result in a 401 error response
  // instead of redirecting to the login url.
  def formIndexJson = RequiresAuthentication("FormClient", "", true) { profile =>
    Action { request =>
      val content = views.html.security.protectedIndex.render(profile)
      val json = Json.obj("content" -> content.toString())
      Ok(json).as("application/json")
    }
  }

  def basicauthIndex = RequiresAuthentication("BasicAuthClient") { profile =>
    Action { request =>
      Ok(views.html.security.protectedIndex(profile))
    }
  }

  def casIndex = RequiresAuthentication("CasClient") { profile =>
    Action { request =>
      Ok(views.html.security.protectedIndex(profile))
    }
  }

  def googleOpenIdIndex = RequiresAuthentication("GoogleOpenIdClient") { profile =>
    Action { request =>
      Ok(views.html.security.protectedIndex(profile))
    }
  }

  def theForm = Action { request =>
    val formClient = Config.getClients().findClient("FormClient").asInstanceOf[FormClient]
    Ok(views.html.security.theForm.render(formClient.getCallbackUrl()))
  }
}
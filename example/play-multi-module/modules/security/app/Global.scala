//package security

import scala.concurrent.Future
import play.api._
import play.api.mvc._
import org.pac4j.play._
import org.pac4j.core.client._
import org.pac4j.cas.client._
import org.pac4j.oauth.client._
import org.pac4j.http.client._
import org.pac4j.openid.client._
import org.pac4j.http.credentials._
import play.api.mvc.Results._
import play.api.Play.current

import com.typesafe.config.ConfigFactory

object Global extends GlobalSettings {

  override def onError(request: RequestHeader, t: Throwable) = {
    Future.successful(InternalServerError(
      views.html.security.error500.render()
    ))
  }

  override def onStart(app: Application) {
    Config.setErrorPage401(views.html.security.error401.render().toString())
    Config.setErrorPage403(views.html.security.error403.render().toString())
    
    val baseUrl =  Play.application.configuration.getString("application.baseUrl").get
    //val baseUrl = Play.application.configuration.getString("baseUrl").get
   
    // OAuth
    val facebookClient = new FacebookClient("132736803558924", "e461422527aeedb32ee6c10834d3e19e")
    val twitterClient = new TwitterClient("HVSQGAw2XmiwcKOTvZFbQ", "FSiO9G9VRR4KCuksky0kgGuo8gAVndYymr4Nl7qc8AA")

    // HTTP
   // val formClient = new FormClient("http://localhost:9000/theForm", new SimpleTestUsernamePasswordAuthenticator())
     val formClient = new FormClient(controllers.security.routes.LoginController.theForm().toString(), new SimpleTestUsernamePasswordAuthenticator())
    val basicAuthClient = new BasicAuthClient(new SimpleTestUsernamePasswordAuthenticator())
    
    
    // CAS
    val casClient = new CasClient()
    //casClient.setGateway(true)
    //casClient.setLogoutHandler(new PlayLogoutHandler())
    casClient.setCasLoginUrl("http://localhost:8080/cas/login")

        // OpenID
        val googleOpenIdClient = new GoogleOpenIdClient()
  // println(org.pac4j.play.routes.CallbackController.callback().toString())
  // println( Play.application.configuration.getString("application.baseUrl").get)
   
    //"/callback" 
    val callbackUrl = org.pac4j.play.routes.CallbackController.callback().toString()
    val clients = new Clients(baseUrl + callbackUrl, facebookClient, twitterClient, formClient, basicAuthClient, casClient, googleOpenIdClient)
    Config.setClients(clients)
    // for test purposes : profile timeout = 60 seconds
    // Config.setProfileTimeout(60)
  }  
}
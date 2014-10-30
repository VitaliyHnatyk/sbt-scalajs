package controllers.security

import play.api.mvc._
import play.api.Routes


object Assets extends controllers.AssetsBuilder

object WebJarAssets extends controllers.WebJarAssets(Assets)

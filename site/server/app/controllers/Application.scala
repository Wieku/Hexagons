package controllers

import javax.inject.Inject

import play.api.Configuration
import play.api.libs.ws.WSClient
import play.api.mvc._

class Application @Inject() (ws: WSClient, conf: Configuration) extends Controller {
  def index = Action { request =>
    Ok(views.html.index())
  }

  def register = Action { request =>
    Ok(views.html.register(false))
  }
}

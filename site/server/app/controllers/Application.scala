package controllers

import javax.inject.Inject

import play.Configuration
import play.api.db.Database
import play.api.mvc._


class Application @Inject() (db: Database, conf: Configuration) extends Controller {


  def index = Action { request =>
    Ok(views.html.index())
  }

  def download = Action { request =>
    Ok(views.html.download())
  }

  def maps = Action { request =>
    Ok(views.html.maps())
  }

  def welcome = Action { request =>
    request.session.data.get("uid") match {
      case Some(_ :String) => Ok(views.html.welcome(request.session.data("name")))
      case _ => Redirect("/login")
    }
  }

  def profile = Action { request =>
    request.session.data.get("uid") match {
      case Some(_ :String) => Ok(views.html.profile(request.session.data("name")))
      case _ => Redirect("/login")
    }
  }


}

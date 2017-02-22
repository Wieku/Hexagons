package controllers

import java.util.UUID
import javax.inject.Inject

import models.{Profile, SqlUtil}
import play.Configuration
import play.api.db.Database
import play.api.libs.json.{JsBoolean, JsNumber, JsObject, JsString}
import play.api.mvc._

class Creator @Inject() (db: Database, conf: Configuration) extends Controller {
  private val qGetMapInfo = SqlUtil.getQuery("map/getMapInfo")

  def mapRegister = Action { request =>
    request.session.data.get("uid") match {
      case Some(id :String) => {
        val profile = new Profile(id.toInt, request.session.data("name"))
        Ok(views.html.mapRegister(profile))
      }
      case _ => Redirect("/login")
    }
  }

  ////////
  // API part

  def getMapInfo(uuid: UUID) = Action { request =>
    request.session.data.get("uid") match {
      case Some(id :String) => {
        db.withConnection { conn =>

          val stmt = conn.prepareStatement(qGetMapInfo)
          stmt.setString(1, uuid.toString)
          val rs = stmt.executeQuery()
          if(rs.next()) {
            Ok(JsObject(Seq(
              "registered" -> JsBoolean(true),
              "name" -> JsString(rs.getString(1)),
              "owner" -> JsNumber(rs.getInt(2))
            )))
          } else {
            Ok(JsObject(Seq(
              "registered" -> JsBoolean(false)
            )))
          }

        }
      }
      case _ => Status(500)
    }
  }
}

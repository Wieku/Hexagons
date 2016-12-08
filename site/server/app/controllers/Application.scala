package controllers

import java.sql.SQLException
import javax.inject.Inject

import com.nimbusds.jose.JWSObject
import com.nimbusds.jose.crypto.MACVerifier
import models.SqlUtil
import play.Configuration
import play.api.db.Database
import play.api.mvc._

import scala.collection.JavaConversions._

class Application @Inject() (db: Database, conf: Configuration) extends Controller {
  private val qGetUserName = SqlUtil.getQuery("getUserName")
  private val qSetUserName = SqlUtil.getQuery("setUserName")

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
    Ok(views.html.welcome())
  }

  def register(token: String) = Action { request =>
    val uid = JWSObject.parse(token)
    if(uid.verify(new MACVerifier(conf.getIntList("hexite.rankservSecret").map(_.byteValue()).toArray))) {
      val id = uid.getPayload.toString

      val conn = db.getConnection
      val stmt = conn.prepareStatement(qGetUserName)
      stmt.setInt(1, id.toInt)
      val rs = stmt.executeQuery()
      if(rs.next()) {
        Ok(views.html.register(err = false, rs.getString(1))).withSession("uid" -> id)
      } else {
        Status(500)
      }

    } else {
      Redirect("/error/auth")
    }
  }

  def registerPost() = Action { request =>
    val id = request.session.data("uid")

    if(id != null) {
      val conn = db.getConnection
      val stmt = conn.prepareStatement(qGetUserName)
      stmt.setInt(1, id.toInt)
      val rs = stmt.executeQuery()
      if(rs.next()) {
        val userName = rs.getString(1)
        if(userName.matches("""^u\d+$""")) {
          if(request.body.asFormUrlEncoded.get("nick") != null) {
            val newNick = request.body.asFormUrlEncoded.get("nick").head
            if(!newNick.matches("""^u\d+$""")) {
              val ustmt = conn.prepareStatement(qSetUserName)
              ustmt.setString(1, newNick)
              ustmt.setInt(2,id.toInt)
              try {
                ustmt.executeUpdate()
                Redirect("/welcome")
              } catch {
                case _: SQLException => Ok(views.html.register(err = true, rs.getString(1))).withSession("uid" -> id)
                case e: Exception => throw new Exception(e)
              }
            } else {
              Ok(views.html.register(err = true, rs.getString(1))).withSession("uid" -> id)
            }
          } else {
            Status(500) // No nick in post form
          }
        } else {
          Status(500) // Already changed
        }
      } else {
        Status(500) // No account / wtf?
      }
    } else {
      Status(500) // no id in session
    }
  }
}

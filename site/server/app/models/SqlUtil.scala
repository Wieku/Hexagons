package models

import java.io.IOException
import java.nio.charset.StandardCharsets

import com.google.common.io.Resources
import play.api.Play.current

object SqlUtil {
  def getQuery(query: String): String = {
    try {
      Resources.toString(Resources.getResource("sql/" + query + ".sql"), StandardCharsets.UTF_8)
    } catch {
      case e: IOException =>
        e.printStackTrace()
        null
    }
  }

  def getIntForQuery(query: String, params: String*)(): Int = {
    play.api.db.DB.withConnection(conn => {
      val statement = conn.prepareStatement(query)
      params.zipWithIndex.foreach {
        case (p, i) => statement.setString(i + 1, p)
      }
      val rs = statement.executeQuery()
      if(rs.next()) rs.getInt(1) else -1
    })
  }

  def getIntForQueryI(query: String, params: Int*)(): Int = {
    play.api.db.DB.withConnection(conn => {
      val statement = conn.prepareStatement(query)
      params.zipWithIndex.foreach {
        case (p, i) => statement.setInt(i + 1, p)
      }
      val rs = statement.executeQuery()
      if(rs.next()) rs.getInt(1) else -1
    })
  }
}
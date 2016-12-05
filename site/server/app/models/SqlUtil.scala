package models

import java.io.IOException
import java.nio.charset.StandardCharsets

import com.google.common.io.Resources

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
}
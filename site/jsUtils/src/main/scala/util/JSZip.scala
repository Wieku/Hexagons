package util

import scala.scalajs.js

object JSZip extends js.Object {

}

class JSZip(data: js.Any = null, options: js.Dynamic = null) extends js.Object {
  var files: js.Dictionary[ZipFile] = js.native

  def folder(name : String) : js.Any = js.native
  def file(name : String, content : String = null) : ZipFile = js.native
  def generate(options : js.Dynamic = null) : js.Any = js.native
  def file(name : String) : ZipFile = js.native
  def load(data: js.Any = null, options: js.Dynamic = null): js.Any = js.native
}

class ZipEntry extends js.Object {
  def isEncrypted(): Boolean = js.native
  def useUTF8(): Boolean = js.native
}

trait ZipFile extends js.Object {
  def asText(): String
}

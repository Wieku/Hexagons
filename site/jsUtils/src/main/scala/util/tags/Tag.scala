package util.tags

import org.scalajs.dom._
import org.scalajs.dom.raw.HTMLElement

abstract class Tag(inner: Tag*) {
  def getNode: Node

}

object Tag {
  implicit def tagOfString(str: String): Tag = {
    new Tag() {
      val element = document.createElement("span")
      element.innerHTML = str.replaceAll(">","&gt;").replaceAll("<","&lt;")
      override def getNode: Node = element
    }
  }

  implicit def tagOfElement(element: Element): Tag = {
    new Tag() {
      override def getNode: Node = element
    }
  }

  implicit def elementTag(tag: Tag): Element = tag.getNode.asInstanceOf[HTMLElement]
}

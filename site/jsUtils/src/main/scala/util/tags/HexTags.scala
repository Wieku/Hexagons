package util.tags

import org.scalajs.dom.MouseEvent
import org.scalajs.dom.raw.HTMLInputElement

object HexTags {
  case class hexButton(text: String, action: ()=>Unit) extends Tags.input {
    private val elem = this.getNode.asInstanceOf[HTMLInputElement]
    elem.`type` = "submit"
    elem.classList.add("hex-button")
    elem.value = text

    elem.onclick = { (e: MouseEvent) =>
      action()
    }
  }

  case class checkbox(id: String) extends Tags.input {
    private val elem = this.getNode.asInstanceOf[HTMLInputElement]
    elem.`type` = "checkbox"
    elem.id = id

    def checked = elem.checked
  }
}

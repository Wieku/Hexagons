import org.scalajs.dom
import dom.{File, MouseEvent, document, window}
import org.scalajs.dom.raw.{FileReader, HTMLElement, HTMLInputElement, HTMLLabelElement}
import util.{JSZip, REST}
import util.tags.HexTags.{checkbox, hexButton}
import util.tags.Tag
import util.tags.Tags._

import scala.scalajs.js
import scala.scalajs.js.{JSApp, JSON, URIUtils}
import js.Dynamic.{global => g}
import scala.collection.mutable

object MapRegister extends JSApp {
  def main(): Unit = {
    val fileInput = document.getElementsByClassName("register-file")(0).asInstanceOf[HTMLInputElement]
    val nextButton = document.getElementsByClassName("register-button")(0).asInstanceOf[HTMLInputElement]

    fileInput.onchange = { (event: dom.Event) =>
      document.getElementsByClassName("register-file-label")(0).asInstanceOf[HTMLElement].innerHTML = fileInput.files.apply(0).name;
    }

    nextButton.onclick = { (event: MouseEvent) =>
      val file = fileInput.files.apply(0)
      processFile(file)
    }
  }

  private def processFile(file: File): Unit = {
    val reader = new FileReader()

    reader.onload = { e: dom.Event =>
      val zip = new JSZip(reader.result)
      val level = JSON.parse(zip.files("map.json").asText()).asInstanceOf[Level]
      processLevel(level, zip)
    }

    reader.readAsArrayBuffer(file)
  }

  private def processLevel(level: Level, zip: JSZip): Unit = {
    val contentRoot = document.getElementsByClassName("register-content")(0).asInstanceOf[HTMLElement]
    while(contentRoot.firstChild != null) contentRoot.removeChild(contentRoot.firstChild)

    val progressList = div()

    contentRoot.appendChild(progressList)

    progressList.appendChild(p(b("Map name: "), span(level.name).withClass("register-bright")))
    progressList.appendChild(p(b("UUID: "), span(level.uuid).withClass("register-bright")))
    progressList.appendChild(p(b("Author: "), span(level.author).withClass("register-bright")))
    progressList.appendChild(p(b("Pack: "), span(level.pack).withClass("register-bright")))
    progressList.appendChild(p(b("Description: "), span(level.description).withClass("register-bright")))
    progressList.appendChild(p(b("Soundtrack author: "), span(level.songAuthor).withClass("register-bright")))
    progressList.appendChild(p(b("Soundtrack name: "), span(level.songName).withClass("register-bright")))


    progressList.appendChild(p(b(s"Is that correct?")))

    val buttonNope = hexButton("Nope", () => window.location.reload())
    val buttonYes = hexButton("Yes!", () => checkLevel(level, zip, progressList))

    progressList.appendChild(p(span(buttonYes), span().withClass("hex-space"), span(buttonNope)))

    println(level.name)
  }

  private def checkLevel(level: Level, zip: JSZip, progressList: Tag): Unit = {
    while(progressList.firstChild != null) progressList.removeChild(progressList.firstChild)
    progressList.appendChild(p(b("Processing..")))
    progressList.appendChild(p("Checking UUID ownership.."))

    REST.get(s"/creator/api/map/info?uuid=${level.uuid}", response => {
      val info = response.asInstanceOf[MapInfo]
      if(info.registered) {
        if(info.owner != g.config.uid) { //TODO: check!!!
          progressList.appendChild(p("ERROR: Map with this UUID was registered by someone else!"))
          return
        }
        progressList.appendChild(p("Map with this UUID exists, no need to set name"))
        progressList.appendChild(p("Adding new revision with this UUID won't wipe old scores"))

        addRevision(level, zip, progressList)
      } else
        askName(level, zip, progressList)
    })
  }

  private def askName(level: Level, zip: JSZip, progressList: Tag): Unit = {
    progressList.appendChild(p(b("Name will be set to "), span(level.name).withClass("register-bright")))
    progressList.appendChild(p("Do you want to continue?"))

    val buttonNope = hexButton("No", () => window.location.reload())
    val buttonYes = hexButton("Yes!", () => registerName(level, zip, progressList))
  }

  private def registerName(level: Level, zip: JSZip, progressList: Tag): Unit = {
    progressList.appendChild(p("Working.."))
    REST.get(s"/creator/api/map/name?uuid=${level.uuid}&name=" + URIUtils.encodeURI(level.name), response => {
      if(!response.asInstanceOf[Boolean]) {
        progressList.appendChild(p("ERROR: Couldn't register name"))
        return
      }
      progressList.appendChild(p("Name registered"))
    })
  }

  private def addRevision(level: Level, zip: JSZip, progressList: Tag): Unit = {
    progressList.appendChild(p("Select files that need checking before game"))
    val inputs = new mutable.HashMap[String, checkbox]
    val list = table()
    var n = 1

    zip.files.keys.foreach(file => {
      if(!file.endsWith("/") && !file.equals("ranked.json")) {
        val box = checkbox(String.valueOf(n))
        if(!Array(".lua", ".hocon", ".json").forall(s => !file.endsWith(s))) {
          box.getNode.asInstanceOf[HTMLInputElement].checked = true
        }

        inputs += file -> box
        list.appendChild(tr(td(box), td(label(htmlFor = String.valueOf(n), file))))
        n += 1
      }
    })
    progressList.appendChild(div(div(list)).withClass("util-flex-center"))
  }
}

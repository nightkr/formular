import forms._
import forms.react.FormWidget
import japgolly.scalajs.react.React
import microjson.JsValue
import org.scalajs.dom
import prickle._

import scala.scalajs.js
import scala.scalajs.js.annotation.JSExport

@JSExport
object Main extends js.JSApp {
  @JSExport
  override def main(): Unit = {
    implicit val pconfig: PConfig[JsValue] = PConfig.Default.copy(areSharedObjectsSupported = false)

    val form: Seq[Field] = (1 to 5).flatMap(id => Seq(
      TextField(id = id, label = s"String $id", minLength = id),
      IntField(id = 10000 + id, label = s"Int $id", required = true, min = Some(5), max = Some(500))
    ))

    // Render the UI!
    // React.render(Form.RenderEdit(form), dom.document.getElementById("main"))
    val pickled: String = Pickle.intoString(form)
    val unpickled = Unpickle[Seq[Field]].fromString(pickled).get
    React.render(FormWidget.edit(unpickled), dom.document.getElementById("main"))

    // Serialize for Matt's sake!
    println(pickled)
  }
}

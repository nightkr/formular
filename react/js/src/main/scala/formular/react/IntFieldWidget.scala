package formular.react

import formular.IntField
import formular.react.IntFieldWidget.{EditProps, EditState}
import japgolly.scalajs.react.vdom.prefix_<^._
import japgolly.scalajs.react.{BackendScope, ReactComponentB, ReactEventI}

import scala.util.{Failure, Success, Try}

class IntFieldWidgetEditBackend($: BackendScope[EditProps, EditState]) {
  def onChange(event: ReactEventI): Unit = {
    object AsInt {
      def unapply(x: String): Option[Int] = Try(x.toInt).toOption
    }

    val value = event.target.value
    $.props.change(value match {
      case "" => Success(None)
      case AsInt(x) => Success(Some(x))
      case _ => Failure(new NumberFormatException("Must be a valid integer"))
    })
    $.modState(_.copy(tmpValue = value))
  }
}

object IntFieldWidget {

  val edit = ReactComponentB[EditProps]("IntFieldWidget_edit")
    .initialState(EditState(""))
    .backend(new IntFieldWidgetEditBackend(_))
    .render((props, _, state, backend) =>
    <.div(
      <.label(
        props.field.label,
        ": ",
        <.input(
          ^.name := s"field-${props.field.id}",
          ^.`type` := "text",
          ^.value := props.value.map(_.toString).getOrElse(state.tmpValue),
          ^.onChange ==> backend.onChange
        )
      ),
      None.orElse(props.validationMsg).getOrElse(""): String
    )
    )
    .build

  case class EditProps(field: IntField, value: Option[Int], validationMsg: Option[String], change: Try[Option[Int]] => Unit)

  case class EditState(tmpValue: String)
}

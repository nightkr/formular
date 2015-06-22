package forms.react

import forms.react.FormWidget.EditState
import forms.{Field, FieldState}
import japgolly.scalajs.react.vdom.prefix_<^._
import japgolly.scalajs.react.{BackendScope, ReactComponentB, ReactEvent}
import prickle.Pickle

class FormWidgetEditBackend($: BackendScope[Seq[Field], EditState]) {
  def onFieldStateChange(id: Int, newValue: FieldState) = $.modState(s => s.copy(fieldStates = s.fieldStates + (id -> newValue)))

  def onSubmit(event: ReactEvent): Unit = {
    event.preventDefault()

    val allValid = $.props.forall(field => field.validateState($.state.fieldStates(field.id)).isEmpty)
    if (allValid) {
      println(Pickle.intoString($.state))
    } else {
      println("Invalid fields!")
    }
  }
}

object FormWidget {

  case class EditState(fieldStates: Map[Int, FieldState])

  val edit = ReactComponentB[Seq[Field]]("FormWidget")
    .getInitialState(props => EditState(fieldStates = props.map(field => (field.id, field.defaultState)).toMap))
    .backend(new FormWidgetEditBackend(_))
    .render((fields, $, state, backend) =>
    <.form(
      ^.onSubmit ==> backend.onSubmit,
      <.div(
        fields.map { field =>
          FieldWidget.edit(field, state.fieldStates(field.id), backend.onFieldStateChange(field.id, _))(): TagMod
        }: _*
      ),
      <.input(
        ^.`type` := "submit"
      )
    )
    )
    .build
}

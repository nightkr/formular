package formular.react

import formular.react.FormWidget.{EditProps, EditState}
import formular.{Field, FieldState}
import japgolly.scalajs.react.vdom.prefix_<^._
import japgolly.scalajs.react.{BackendScope, ReactComponentB, ReactEvent}

class FormWidgetEditBackend($: BackendScope[EditProps, EditState]) {
  def onFieldStateChange(id: Int, newValue: FieldState) = $.modState(s => s.copy(fieldStates = s.fieldStates + (id -> newValue)))

  def onSubmit(event: ReactEvent): Unit = {
    event.preventDefault()

    val validated = $.props.fields.map(field => field.id -> field.validateState($.state.fieldStates(field.id)))
    val allValid = validated.forall(_._2.isEmpty)
    if (allValid) {
      $.props.onSubmit($.state.fieldStates)
    } else {
      val errors = validated.flatMap { case (id, error) => error.map(id -> _) }
      $.props.onSubmitFailure(errors.toMap)
    }
  }
}

object FormWidget {

  val edit = ReactComponentB[EditProps]("FormWidget")
    .getInitialState(props => EditState(fieldStates = props.fields.map(field => (field.id, field.defaultState)).toMap))
    .backend(new FormWidgetEditBackend(_))
    .render((props, $, state, backend) =>
    <.form(
      ^.onSubmit ==> backend.onSubmit,
      <.div(
        props.fields.map { field =>
          FieldWidget.edit(field, state.fieldStates(field.id), backend.onFieldStateChange(field.id, _))(): TagMod
        }: _*
      ),
      <.input(
        ^.`type` := "submit"
      )
    )
    )
    .build

  case class EditProps(fields: Seq[Field], onSubmit: Map[Int, FieldState] => Unit, onSubmitFailure: Map[Int, String] => Unit = identity)

  case class EditState(fieldStates: Map[Int, FieldState])

}

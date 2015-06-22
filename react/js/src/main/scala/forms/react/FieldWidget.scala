package forms.react

import forms._

import scala.util.Try

object FieldWidget {
  def edit(field: Field, state: FieldState, change: FieldState => Unit) = field match {
    case field: TextField =>
      val decodedState = field.decodeAndValidateState(state)
      def onChange(x: String): Unit = change(field.encodeState(Right(x)))
      TextFieldWidget.edit.withProps(TextFieldWidget.EditProps(field, decodedState.right.toOption, decodedState.left.toOption, onChange))
    case field: IntField =>
      val decodedState = field.decodeAndValidateState(state)
      def onChange(x: Try[Option[Int]]): Unit = change(field.encodeState(x.toEither))
      IntFieldWidget.edit.withProps(IntFieldWidget.EditProps(field, decodedState.right.toOption.flatten, decodedState.left.toOption, onChange))
  }
}

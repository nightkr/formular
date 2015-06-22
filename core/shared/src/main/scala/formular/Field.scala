package formular

import microjson.{JsString, JsValue}
import prickle._

import scala.collection.mutable
import scala.reflect.ClassTag
import scala.util.{Failure, Success, Try}

sealed trait Field {
  def id: Int

  def defaultState: FieldState

  def validateState(fieldState: FieldState): Option[String]
}

object Field {
  implicit def picklerPair: PicklerPair[Field] =
    CompositePickler[Field]
      .concreteType[TextField]
      .concreteType[IntField]
}

case class FieldState(value: JsValue)

object FieldState {
  // TODO: Store JSON tree directly instead?
  private implicit def jsValuePickler: PicklerPair[JsValue] = CompositePickler[JsValue]
    .concreteType[microjson.JsNull.type]
    .concreteType[microjson.JsTrue.type]
    .concreteType[microjson.JsFalse.type]
    .concreteType[microjson.JsNumber]
    .concreteType[microjson.JsString]
    .concreteType[microjson.JsArray]
    .concreteType[microjson.JsObject]

  implicit def fieldStatePickler: Pickler[FieldState] = Pickler.materializePickler[FieldState]

  implicit def fieldStateUnpickler: Unpickler[FieldState] = Unpickler.materializeUnpickler[FieldState]
}

sealed trait BaseField extends Field {
  type Value

  def encodeState(value: Either[String, Value]): FieldState

  def decodeState(fieldState: FieldState): Try[Either[String, Value]]

  def validateDecodedState(value: Value): Option[String]

  def decodeAndValidateState(fieldState: FieldState): Either[String, Value] =
    decodeState(fieldState) match {
      case Success(Right(x)) =>
        validateDecodedState(x).fold[Either[String, Value]](Right(x))(Left(_))
      case Success(Left(x)) => Left(x)
      case Failure(ex) =>
        Left(ex.getMessage)
    }

  override def validateState(fieldState: FieldState): Option[String] = decodeAndValidateState(fieldState).left.toOption
}

case class TextField(id: Int, label: String, minLength: Int) extends BaseField {
  override type Value = String

  override def encodeState(value: Either[String, String]): FieldState = FieldState(Pickle(value))

  override def decodeState(fieldState: FieldState): Try[Either[String, String]] = Unpickle[Either[String, String]].from(fieldState.value)

  override def validateDecodedState(value: String): Option[String] =
    if (value.length < minLength)
      Some(s"Must be at least $minLength characters long")
    else
      None

  override def defaultState: FieldState = encodeState(Right(""))
}

case class IntField(id: Int, label: String, required: Boolean, min: Option[Int] = None, max: Option[Int] = None) extends BaseField {
  override type Value = Option[Int]

  override def encodeState(value: Either[String, Option[Int]]): FieldState = FieldState(Pickle(value))

  override def decodeState(fieldState: FieldState): Try[Either[String, Option[Int]]] = Unpickle[Either[String, Option[Int]]].from(fieldState.value)

  override def validateDecodedState(value: Option[Int]): Option[String] =
    if (required && value.isEmpty)
      Some("Required")
    else if (value.flatMap(x => min.map(min => x < min)).getOrElse(false))
      Some(s"Must be at least ${min.get}")
    else if (value.flatMap(x => max.map(max => x > max)).getOrElse(false))
      Some(s"Must not be more than ${max.get}")
    else
      None

  override def defaultState: FieldState = encodeState(Right(None))
}

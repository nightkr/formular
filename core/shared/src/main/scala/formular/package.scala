import prickle._

import scala.collection.mutable
import scala.util.{Failure, Success, Try}

package object formular {
  implicit def leftPickler[A, B](implicit ev: Pickler[A]): Pickler[Left[A, B]] = new Pickler[Left[A, B]] {
    override def pickle[P](obj: Left[A, B], state: PickleState)(implicit config: PConfig[P]): P = ev.pickle(obj.a, state)
  }

  implicit def rightPickler[A, B](implicit ev: Pickler[B]): Pickler[Right[A, B]] = new Pickler[Right[A, B]] {
    override def pickle[P](obj: Right[A, B], state: PickleState)(implicit config: PConfig[P]): P = ev.pickle(obj.b, state)
  }

  implicit def leftUnpickler[A, B](implicit ev: Unpickler[A]): Unpickler[Left[A, B]] = new Unpickler[Left[A, B]] {
    override def unpickle[P](pickle: P, state: mutable.Map[String, Any])(implicit config: PConfig[P]): Try[Left[A, B]] = ev.unpickle(pickle, state).map(Left.apply)
  }

  implicit def rightUnpickler[A, B](implicit ev: Unpickler[B]): Unpickler[Right[A, B]] = new Unpickler[Right[A, B]] {
    override def unpickle[P](pickle: P, state: mutable.Map[String, Any])(implicit config: PConfig[P]): Try[Right[A, B]] = ev.unpickle(pickle, state).map(Right.apply)
  }

  implicit def eitherPickler[A: Pickler : Unpickler, B: Pickler : Unpickler]: PicklerPair[Either[A, B]] =
    CompositePickler[scala.util.Either[A, B]]
      .concreteType[scala.util.Left[A, B]]
      .concreteType[scala.util.Right[A, B]]

  implicit class TryToEither[T](attempt: Try[T]) {
    def toEither: Either[String, T] = attempt match {
      case Failure(ex) => Left(ex.getMessage)
      case Success(x) => Right(x)
    }
  }

}

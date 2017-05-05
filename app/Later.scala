import cats.Monad

import scala.concurrent.{ExecutionContext, Future}

sealed trait Later[T]

object Later {

  def await[T](f: (Int) => Future[(Int, T)]): Later[T] = Await(f)

  private case class Await[T](f: (Int) => Future[(Int, T)]) extends Later[T]

  private case class FlatMap[A, B](fa: Later[A], f: A => Later[B]) extends Later[B]

  private case class TailRecM[A, B](a: A, f: (A) => Later[Either[A, B]]) extends Later[B]

  implicit object Instance extends Monad[Later] {
    override def pure[A](x: A): Later[A] =
      Await(t => Future.successful(t -> x))

    override def flatMap[A, B](fa: Later[A])(f: (A) => Later[B]): Later[B] = FlatMap(fa, f)

    override def tailRecM[A, B](a: A)(f: (A) => Later[Either[A, B]]): Later[B] =
      TailRecM(a, f)
  }

  def run[T](later: Later[T])(implicit ec: ExecutionContext) = runToken(0)(later).map(_._2)

  def runToken[T](token: Int)(later: Later[T])(implicit ec: ExecutionContext): Future[(Int, T)] =
    later match {
      case Await(f) =>
        f(token)
      case FlatMap(fa, f) =>
        runToken(token)(fa).flatMap {
          case (token, ffa) =>
            runToken(token)(f(ffa))
        }
      case TailRecM(a, f) =>
        runToken(token)(f(a)).flatMap {
          case (to, Right(b)) => Future.successful(to -> b)
          case (to, Left(a))  => runToken(to)(TailRecM(a, f))
        }
    }
}

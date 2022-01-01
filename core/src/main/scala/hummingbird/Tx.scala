package hummingbird

import cats.effect.Effect

import scala.concurrent.Future
import scala.concurrent.duration.FiniteDuration

trait Tx[+A] { self =>
  type F[+_]
  type EffectT[FF[_]] = TransformK[FF, F]

  type H[+_]
  type StreamT[HH[_]] = TransformK[HH, H]

  //noinspection DuplicatedCode
  type I[-T] <: Rx[T] { type I[-X] = self.I[X]; type J[+X] = self.J[X] }
  //noinspection DuplicatedCode
  type J[+T] <: Tx[T] { type I[-X] = self.I[X]; type J[+X] = self.J[X] }

  private[hummingbird] val stream: H[A]

  def map[B](fn: A => B): J[B]

  def flatMap[B](fn: A => J[B]): J[B]

  def evalMap[B](fn: A => F[B]): J[B]

  def collect[B](pf: PartialFunction[A, B]): J[B]

  def filter(p: A => Boolean): J[A]

  def withLatest[B](other: J[B]): J[(A, B)]

  def withLatestMap[B, C](other: J[B])(fn: (A, B) => C): J[C]

  def scan[B](z: B)(op: (B, A) => B): J[B]

  def scan0[B](z: B)(op: (B, A) => B): J[B]

  def debounce(d: FiniteDuration): J[A]

  def debounceMillis(millis: Long): J[A]

  def async: J[A]

  def delay(duration: FiniteDuration): J[A]

  def delayMillis(millis: Long): J[A]

  def concatMapFuture[B](fn: A => Future[B]): J[B]

  def concatMapAsync[FF[_] : Effect, B](fn: A => FF[B]): J[B]
}

trait TxBuilder { self =>
  type H[+_]
  type StreamT[HH[_]] = TransformK[HH, H]

  type J[+T] <: Tx[T] { type H[+X] <: self.H[X]; type J[+X] = self.J[X] }

  def empty[A]: J[A]

  def withLatest[A, B](a: J[A], b: J[B]): J[(A, B)]

  def withLatestMap[A, B, C](a: J[A], b: J[B])(fn: (A, B) => C): J[C]

}
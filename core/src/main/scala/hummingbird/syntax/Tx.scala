package hummingbird.syntax

import cats.effect.Effect
import hummingbird._

import scala.concurrent.Future
import scala.concurrent.duration.FiniteDuration

trait Tx extends Context { self =>
  def map[A, B](source: H[A])(f: A => B): H[B]

  def flatMap[A, B](source: H[A])(f: A => H[B]): H[B]

  def evalMap[A, B](source: H[A])(f: A => F[B]): H[B]

  def collect[A, B](source: H[A])(pf: PartialFunction[A, B]): H[B]

  def filter[A](source: H[A])(p: A => Boolean): H[A]

  def withLatest[A, B](source: H[A])(other: H[B]): H[(A, B)]

  def withLatestMap[A, B, C](source: H[A])(other: H[B])(f: (A, B) => C): H[C]

  def scan[A, B](source: H[A])(z: B)(op: (B, A) => B): H[B]

  def scan0[A, B](source: H[A])(z: B)(op: (B, A) => B): H[B]

  def debounce[A](source: H[A])(d: FiniteDuration): H[A]

  def debounceMillis[A](source: H[A])(millis: Long): H[A]

  def async[A](source: H[A]): H[A]

  def delay[A](source: H[A])(duration: FiniteDuration): H[A]

  def delayMillis[A](source: H[A])(millis: Long): H[A]

  def concatMapFuture[A, B](source: H[A])(f: A => Future[B]): H[B]

  def concatMapAsync[A, FF[_] : Effect, B](source: H[A])(f: A => FF[B]): H[B]

  def redirect[A, B](source: H[A])(transform: G[B] => G[A]): H[B]

  def subscribe[A](source: H[A])(subscriber: G[A]): Cancelable
}
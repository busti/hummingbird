package hummingbird.interop.fs2.syntax

import cats.effect.{Concurrent, Effect, Timer}
import fs2._
import hummingbird.interop.fs2.Fs2Context
import hummingbird.syntax.Tx

import scala.concurrent.Future
import scala.concurrent.duration.FiniteDuration

class Fs2Tx[FF[_] : Concurrent : Timer] extends Tx with Fs2Context[FF] {
  def map[A, B](source: H[A])(f: A => B): H[B] =
    source.map(f)

  def flatMap[A, B](source: H[A])(f: A => H[B]): H[B] =
    source.flatMap(f)

  def evalMap[A, B](source: H[A])(f: A => FF[B]): H[B] =
    source.evalMap(f)

  def collect[A, B](source: H[A])(pf: PartialFunction[A, B]): H[B] =
    source.collect(pf)

  def filter[A](source: H[A])(p: A => Boolean): H[A] =
    source.filter(p)

  def withLatest[A, B](source: H[A])(other: H[B]): H[(A, B)] =
    source.zipWith(other)(Tuple2.apply)

  def withLatestMap[A, B, C](source: H[A])(other: H[B])(f: (A, B) => C): H[C] =
    source.zipWith(other)(f)

  def scan[A, B](source: H[A])(z: B)(op: (B, A) => B): H[B] =
    source.scan(z)(op)

  def scan0[A, B](source: H[A])(z: B)(op: (B, A) => B): H[B] =
    source.scan(z)(op)

  def debounce[A](source: H[A])(d: FiniteDuration): H[A] =
    source.debounce(d)

  def debounceMillis[A](source: H[A])(millis: Long): H[A] =
    source.debounce(FiniteDuration(millis, "millis"))

  def async[A](source: H[A]): H[A] = ???

  def delay[A](source: H[A])(duration: FiniteDuration): H[A] =
    source.delayBy(duration)

  def delayMillis[A](source: H[A])(millis: Long): H[A] =
    source.delayBy(FiniteDuration(millis, "millis"))

  def concatMapFuture[A, B](source: H[A])(f: A => Future[B]): H[B] = ???

  def concatMapAsync[A, FFF[_] : Effect, B](source: H[A])(f: A => FFF[B]): H[B] = ???

  def redirect[A, B](source: H[A])(transform: (Pipe[F, B, Unit]) => Pipe[F, A, Unit]): H[B] =
    ???

  def subscribe[A](source: H[A])(subscriber: Pipe[FF, A, Unit]): Any = ???
}

package hummingbird.interop.zio.syntax

import cats.effect.Effect
import hummingbird.interop.zio.ZioContext
import hummingbird.syntax.Tx
import zio._
import zio.stream._

import scala.concurrent.Future
import scala.concurrent.duration.FiniteDuration

class ZioTx[R, E] extends Tx with ZioContext[R with Clock, E] {
  def map[A, B](source: H[A])(f: A => B): H[B] =
    source.map(f)

  def flatMap[A, B](source: H[A])(f: A => H[B]): H[B] =
    source.flatMap(f)

  def evalMap[A, B](source: H[A])(f: A => F[B]): H[B] =
    source.mapZIO(f)

  def collect[A, B](source: H[A])(pf: PartialFunction[A, B]): H[B] =
    source.collect(pf)

  def filter[A](source: H[A])(p: A => Boolean): H[A] =
    source.filter(p)

  def withLatest[A, B](source: H[A])(other: H[B]): H[(A, B)] =
    source.zip(other)

  def withLatestMap[A, B, C](source: H[A])(other: H[B])(f: (A, B) => C): H[C] =
    source.zipWith(other)(f)

  def scan[A, B](source: H[A])(z: B)(op: (B, A) => B): H[B] =
    source.scan(z)(op)

  def scan0[A, B](source: H[A])(z: B)(op: (B, A) => B): H[B] = ???

  def debounce[A](source: H[A])(d: FiniteDuration): H[A] =
    source.debounce(Duration.fromScala(d))

  def debounceMillis[A](source: H[A])(millis: Long): H[A] =
    source.debounce(Duration.fromMillis(millis))

  def async[A](source: H[A]): H[A] = ???

  def delay[A](source: H[A])(duration: FiniteDuration): H[A] = ???

  def delayMillis[A](source: H[A])(millis: Long): H[A] = ???

  def concatMapFuture[A, B](source: H[A])(f: A => Future[B]): H[B] = ???

  def concatMapAsync[A, FF[_] : Effect, B](source: H[A])(f: A => FF[B]): H[B] = ???

  def redirect[A, B](source: ZStream[R with Clock, E, A])(transform: ZSink[R with Clock, E, B, Unit, Unit] => ZSink[R with Clock, E, A, Unit, Unit]): ZStream[R with Clock, E, B] = ???

  def subscribe[A](source: ZStream[R with Clock, E, A])(subscriber: ZSink[R with Clock, E, A, Unit, Unit]): Unit => UIO[Unit] = ???
}

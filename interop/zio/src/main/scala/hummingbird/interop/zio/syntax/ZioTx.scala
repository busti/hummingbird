package hummingbird.interop.zio.syntax

import cats.effect.Effect
import hummingbird.syntax.Tx
import zio._
import zio.stream._

import scala.concurrent.Future
import scala.concurrent.duration.FiniteDuration

class ZioTx[+A](val stream: UStream[A]) extends Tx[A] {
  type F[+T] = Task[T]
  type H[+T] = UStream[T]
  type I[-T] = ZioRx[T]
  type J[+T] = ZioTx[T]

  override def subscribe(subscriber: ZioRx[_ <: A]) =
    stream.run(subscriber.stream)

  override def map[B](f: A => B): ZioTx[B] = ???

  override def flatMap[B](f: A => ZioTx[B]): ZioTx[B] = ???

  override def evalMap[B](f: A => Task[B]): ZioTx[B] = ???

  override def collect[B](pf: PartialFunction[A, B]): ZioTx[B] = ???

  override def filter(p: A => Boolean): ZioTx[A] = ???

  override def withLatest[B](other: ZioTx[B]): ZioTx[(A, B)] = ???

  override def withLatestMap[B, C](other: ZioTx[B])(f: (A, B) => C): ZioTx[C] = ???

  override def scan[B](z: B)(op: (B, A) => B): ZioTx[B] = ???

  override def scan0[B](z: B)(op: (B, A) => B): ZioTx[B] = ???

  override def debounce(d: FiniteDuration): ZioTx[A] = ???

  override def debounceMillis(millis: Long): ZioTx[A] = ???

  override def async: ZioTx[A] = ???

  override def delay(duration: FiniteDuration): ZioTx[A] = ???

  override def delayMillis(millis: Long): ZioTx[A] = ???

  override def concatMapFuture[B](f: A => Future[B]): ZioTx[B] = ???

  override def concatMapAsync[FF[_] : Effect, B](f: A => FF[B]): ZioTx[B] = ???

  override def redirect[B](transform: ZioRx[_ >: B] => ZioRx[A]): ZioTx[B] = ???
}

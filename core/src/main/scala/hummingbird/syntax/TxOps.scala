package hummingbird.syntax

import cats.effect.Effect
import hummingbird.Context

import scala.concurrent.Future
import scala.concurrent.duration.FiniteDuration

class TxOps[HH[+_], A](val source: HH[A]) extends AnyVal with Context { self =>
  type H[+T] = HH[T]

  //noinspection DuplicatedCode
  type TTx <: Tx {
    type Cancelable = self.Cancelable
    type F[+T] = self.F[T]
    type G[-T] = self.G[T]
    type H[+T] = self.H[T]
  }

  def map[B](f: A => B)(implicit tx: TTx): H[B] = tx.map[A, B](source)(f)

  def flatMap[B](f: A => H[B])(implicit tx: TTx): H[B] = tx.flatMap[A, B](source)(f)

  def evalMap[B](f: A => F[B])(implicit tx: TTx): H[B] = tx.evalMap[A, B](source)(f)

  def collect[B](pf: PartialFunction[A, B])(implicit tx: TTx): H[B] = tx.collect[A, B](source)(pf)

  def filter(p: A => Boolean)(implicit tx: TTx): H[A] = tx.filter[A](source)(p)

  def withLatest[B](other: H[B])(implicit tx: TTx): H[(A, B)] = tx.withLatest[A, B](source)(other)

  def withLatestMap[B, C](other: H[B])(f: (A, B) => C)(implicit tx: TTx): H[C] = tx.withLatestMap[A, B, C](source)(other)(f)

  def scan[B](z: B)(op: (B, A) => B)(implicit tx: TTx): H[B] = tx.scan[A, B](source)(z)(op)

  def scan0[B](z: B)(op: (B, A) => B)(implicit tx: TTx): H[B] = tx.scan0[A, B](source)(z)(op)

  def debounce(d: FiniteDuration)(implicit tx: TTx): H[A] = tx.debounce[A](source)(d)

  def debounceMillis(millis: Long)(implicit tx: TTx): H[A] = tx.debounceMillis[A](source)(millis)

  def async(implicit tx: TTx): H[A] = tx.async[A](source)

  def delay(duration: FiniteDuration)(implicit tx: TTx): H[A] = tx.delay[A](source)(duration)

  def delayMillis(millis: Long)(implicit tx: TTx): H[A] = tx.delayMillis[A](source)(millis)

  def concatMapFuture[B](f: A => Future[B])(implicit tx: TTx): H[B] = tx.concatMapFuture[A, B](source)(f)

  def concatMapAsync[FF[_] : Effect, B](f: A => FF[B])(implicit tx: TTx): H[B] = tx.concatMapAsync[A, FF, B](source)(f)

  def redirect[B](transform: G[B] => G[A])(implicit tx: TTx): H[B] = tx.redirect[A, B](source)(transform)

  def subscribe(subscriber: G[A])(implicit tx: TTx): Cancelable = tx.subscribe[A](source)(subscriber)
}
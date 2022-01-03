package hummingbird.interop.monix.syntax

import cats.effect.Effect
import hummingbird.syntax.{Tx, TxBuilder}
import monix.eval._
import monix.reactive._

import scala.concurrent.Future
import scala.concurrent.duration.FiniteDuration

class MonixTx[+A](val stream: Observable[A]) extends Tx[A] {
  type F[+T] = Task[T]
  type H[+T] = Observable[T]
  type I[-T] = MonixRx[T]
  type J[+T] = MonixTx[T]

  def map[B](f: A => B): J[B] = new MonixTx(stream.map(f))

  def flatMap[B](f: A => MonixTx[B]): MonixTx[B] = new MonixTx(stream.flatMap(a => f(a).stream))

  def evalMap[B](fn: A => Task[B]): MonixTx[B] = new MonixTx(stream.mapEval(fn))

  def collect[B](pf: PartialFunction[A, B]): MonixTx[B] = new MonixTx(stream.collect(pf))

  def filter(p: A => Boolean): MonixTx[A] = new MonixTx(stream.filter(p))

  def withLatest[B](other: MonixTx[B]): MonixTx[(A, B)] = new MonixTx(stream.withLatestFrom(other.stream)(Tuple2.apply))

  def withLatestMap[B, C](other: MonixTx[B])(fn: (A, B) => C): MonixTx[C] =
    new MonixTx(stream.withLatestFrom(other.stream)(fn))

  def scan[B](z: B)(op: (B, A) => B): MonixTx[B] = new MonixTx(stream.scan(z)(op))

  def scan0[B](z: B)(op: (B, A) => B): MonixTx[B] = new MonixTx(stream.scan0(z)(op))

  def debounce(d: FiniteDuration): MonixTx[A] = new MonixTx(stream.debounce(d))

  def debounceMillis(millis: Long): MonixTx[A] = new MonixTx(stream.debounce(FiniteDuration.apply(millis, "millis")))

  def async: MonixTx[A] = new MonixTx(stream.asyncBoundary(OverflowStrategy.Unbounded))

  def delay(duration: FiniteDuration): MonixTx[A] = new MonixTx(stream.delayExecution(duration))

  def delayMillis(millis: Long): MonixTx[A] = new MonixTx(stream.delayExecution(FiniteDuration.apply(millis, "millis")))

  def concatMapFuture[B](fn: A => Future[B]): MonixTx[B] = ??? // new MonixTx(stream.concatMap(Observable.fromFuture(() => fn())))

  def concatMapAsync[FF[_] : Effect, B](fn: A => FF[B]): MonixTx[B] = ??? // new MonixTx(stream.concatMap(Observable.fromFuture(() => fn())))
}

class MonixTxBuilder extends TxBuilder {
  type H[+T] = Observable[T]
  type J[+T] = MonixTx[T]

  def empty[A]: MonixTx[A] = new MonixTx(Observable.empty)

  def withLatest[A, B](a: MonixTx[A], b: MonixTx[B]): MonixTx[(A, B)] =
    new MonixTx(a.stream.withLatestFrom(b.stream)(Tuple2.apply))

  def withLatestMap[A, B, C](a: MonixTx[A], b: MonixTx[B])(fn: (A, B) => C): MonixTx[C] =
    new MonixTx(a.stream.withLatestFrom(b.stream)(fn))
}
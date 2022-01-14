package hummingbird.interop.monix.syntax

import cats.effect.Effect
import hummingbird.interop.monix.MonixContext
import hummingbird.syntax.Tx
import monix.eval.Task
import monix.execution.{Ack, Scheduler}
import monix.reactive.{Observable, Observer, OverflowStrategy}

import scala.concurrent.Future
import scala.concurrent.duration.FiniteDuration

class MonixTx(implicit scheduler: Scheduler) extends Tx with MonixContext {
  def map[A, B](source: Observable[A])(f: A => B): Observable[B] =
    source.map(f)

  def flatMap[A, B](source: Observable[A])(f: A => Observable[B]): Observable[B] =
    source.flatMap(f)

  def evalMap[A, B](source: Observable[A])(f: A => Task[B]): Observable[B] =
    source.mapEval(f)

  def collect[A, B](source: Observable[A])(pf: PartialFunction[A, B]): Observable[B] =
    source.collect(pf)

  def filter[A](source: Observable[A])(p: A => Boolean): Observable[A] =
    source.filter(p)

  def withLatest[A, B](source: Observable[A])(other: Observable[B]): Observable[(A, B)] =
    source.withLatestFrom(other)(Tuple2.apply)

  def withLatestMap[A, B, C](source: Observable[A])(other: Observable[B])(f: (A, B) => C): Observable[C] =
    source.withLatestFrom(other)(f)

  def scan[A, B](source: Observable[A])(z: B)(op: (B, A) => B): Observable[B] =
    source.scan(z)(op)

  def scan0[A, B](source: Observable[A])(z: B)(op: (B, A) => B): Observable[B] =
    source.scan(z)(op)

  def debounce[A](source: Observable[A])(d: FiniteDuration): Observable[A] =
    source.debounce(d)

  def debounceMillis[A](source: Observable[A])(millis: Long): Observable[A] =
    source.debounce(FiniteDuration(millis, "millis"))

  def async[A](source: Observable[A]): Observable[A] =
    source.asyncBoundary(OverflowStrategy.Default)

  def delay[A](source: Observable[A])(duration: FiniteDuration): Observable[A] =
    source.delayExecution(duration)

  def delayMillis[A](source: Observable[A])(millis: Long): Observable[A] =
    source.delayExecution(FiniteDuration(millis, "millis"))

  def concatMapFuture[A, B](source: Observable[A])(f: A => Future[B]): Observable[B] =
    source.concatMap(next => Observable.fromFuture(f(next)))

  def concatMapAsync[A, FF[_] : Effect, B](source: Observable[A])(f: A => FF[B]): Observable[B] =
    source.concatMap(next => Observable.from(f(next)))

  def redirect[A, B](source: Observable[A])(transform: Observer[B] => Observer[A]): Observable[B] =
  Observable.create[B](OverflowStrategy.Unbounded) { subscriber =>
    source.subscribe(transform(subscriber))
  }

  def subscribe[A](source: Observable[A])(subscriber: Observer[A]): Cancelable =
    source.subscribe(subscriber)
}
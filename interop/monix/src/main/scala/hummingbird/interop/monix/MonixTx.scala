package hummingbird.interop.monix

import cats.Eq
import hummingbird._
import monix.eval._
import monix.reactive._

import scala.concurrent.duration.{Duration, FiniteDuration}
import scala.concurrent.{ExecutionContext, Future}
import scala.util.chaining._

class MonixTx[A](val stream: Observable[A]) extends Tx[A] {
  type F[T] = Task[T]
  type H[T] = Observable[T]

  def async: Observable[A] = stream.executeAsync
  def collect[B](fn: PartialFunction[A, B]): Observable[B] = stream.collect(fn)
  def combineLatest[B](other: Observable[B]): Observable[(A, B)] = stream.combineLatest(other)
  def combineLatestMap[B, R](other: Observable[B])(fn: (A, B) => R): Observable[R] = stream.combineLatestMap(other)(fn)
  def concatMapAsync[B](fn: A => Task[B]): Observable[B] = stream.concatMap(Observable.fromTask[B] compose fn)
  def concatMapFuture[B](fn: A => Future[B])(implicit ec: ExecutionContext): Observable[B] = stream.concatMap(Observable.fromFuture[B] compose fn)
  def debounce(duration: FiniteDuration): Observable[A] = stream.debounce(duration)
  def debounceMillis(millis: Int): Observable[A] = stream.debounce(FiniteDuration(millis, "millis"))
  def delay(duration: FiniteDuration): Observable[A] = stream.delayOnNext(duration)
  def delayMillis(millis: Int): Observable[A] = stream.delayOnNext(FiniteDuration(millis, "millis"))
  def distinct(implicit eq: Eq[A]): Observable[A] = stream.distinctUntilChanged
  def distinctOnEquals: Observable[A] = stream.distinctUntilChanged(Eq.fromUniversalEquals)
  def drop(num: Int): Observable[A] = stream.drop(num)
  def dropWhile(predicate: A => Boolean): Observable[A] = stream.dropWhile(predicate)
  def evalMap[B](fn: A => Task[B]): Observable[B] = stream.mapEval(fn)
  def filter(fn: A => Boolean): Observable[A] = stream.filter(fn)
  def flatMap[B](fn: A => Observable[B]): Observable[B] = stream.flatMap(fn)
  def head: Observable[A] = stream.head
  def map[B](fn: A => B): Observable[B] = stream.map(fn)
  def mapAccumulate[S](seed: S)(fn: (S, A) => S): Observable[(Option[A], S)] = stream.scan[(Option[A], S)](None -> seed) { case ((_, state), next) => Some(next) -> fn(state, next) }
  def mapAccumulate0[S](seed: S)(fn: (S, A) => S): Observable[(Option[A], S)] = stream.scan0[(Option[A], S)](None -> seed) { case ((_, state), next) => Some(next) -> fn(state, next) }
  def mapEither[B](fn: A => Either[Throwable, B]): Observable[B] = stream.mapEval(value => Task.fromEither[Throwable, B](fn(value)))
  def mapFilter[B](fn: A => Option[B]): Observable[B] = stream.map(fn).collect { case Some(value) => value }
  def mergeMap[B](fn: A => Observable[B]): Observable[B] = stream.mergeMap(fn)
  def prepend(value: A): Observable[A] = stream.prepend(value)
  def prependAsync(value: Task[A]): Observable[A] = Observable(Observable.fromTask(value), stream).concat
  def prependFuture(value: Future[A]): Observable[A] = Observable(Observable.fromFuture(value), stream).concat
  def recover(fn: PartialFunction[Throwable, A]): Observable[A] = stream.onErrorRecover(fn)
  def recoverOption(fn: PartialFunction[Throwable, Option[A]]): Observable[A] = stream.onErrorRecoverWith(fn.andThen(_.map(Observable.now).getOrElse(Observable.empty)))
  def scan[S](seed: S)(fn: (S, A) => S): Observable[S] = stream.scan(seed)(fn)
  def scan0[S](seed: S)(fn: (S, A) => S): Observable[S] = stream.scan0(seed)(fn)
  def sample(duration: FiniteDuration): Observable[A] = stream.sample(duration)
  def sampleMillis(millis: Int): Observable[A] = stream.sample(FiniteDuration(millis, "millis"))
  def startsWith(values: Iterable[A]): Observable[A] = stream.startWith(values.toSeq)
  def switchMap[B](fn: A => Observable[B]): Observable[B] = stream.switchMap(fn)
  def take(num: Int): Observable[A] = stream.take(num)
  def takeWhile(predicate: A => Boolean): Observable[A] = stream.takeWhile(predicate)
  def withLatest[B](latest: Observable[B]): Observable[(A, B)] = stream.withLatestFrom(latest)((a, b) => (a, b))
  def withLatestMap[B, R](latest: Observable[B])(fn: (A, B) => R): Observable[R] = stream.withLatestFrom(latest)(fn)
  def zip[B](other: Observable[B]): Observable[(A, B)] = stream.zip(other)
  def zipWithIndex: Observable[(A, Long)] = stream.zipWithIndex
}

class MonixBuildTx extends BuildTx {
  override type H[T] = Observable[T]

  override def combineLatest[A, B](a: Observable[A], b: Observable[B]): Observable[(A, B)] = Observable.combineLatest2(a, b)

  override def combineLatestMap[A, B, R](a: Observable[A], b: Observable[B])(fn: (A, B) => R): Observable[R] = Observable.combineLatestMap2(a, b)(fn)

  override def withLatest[S, A](source: Observable[S], latestA: Observable[A]): Observable[(S, A)] = source.withLatestFrom(latestA)((s, a) => (s, a))
}
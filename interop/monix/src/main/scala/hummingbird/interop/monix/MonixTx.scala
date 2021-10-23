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

  override def async: Observable[A] = stream.executeAsync
  override def collect[B](fn: PartialFunction[A, B]): Observable[B] = stream.collect(fn)
  override def combineLatest[B](other: Observable[B]): Observable[(A, B)] = stream.combineLatest(other)
  override def combineLatestMap[B, R](other: Observable[B])(fn: (A, B) => R): Observable[R] = stream.combineLatestMap(other)(fn)
  override def concatMapAsync[B](fn: A => Task[B]): Observable[B] = stream.concatMap(Observable.fromTask[B] compose fn)
  override def concatMapFuture[B](fn: A => Future[B])(implicit ec: ExecutionContext): Observable[B] = stream.concatMap(Observable.fromFuture[B] compose fn)
  override def debounce(duration: FiniteDuration): Observable[A] = stream.debounce(duration)
  override def debounceMillis(millis: Int): Observable[A] = stream.debounce(FiniteDuration(millis, "millis"))
  override def delay(duration: FiniteDuration): Observable[A] = stream.delayOnNext(duration)
  override def delayMillis(millis: Int): Observable[A] = stream.delayOnNext(FiniteDuration(millis, "millis"))
  override def distinct(implicit eq: Eq[A]): Observable[A] = stream.distinctUntilChanged
  override def distinctOnEquals: Observable[A] = stream.distinctUntilChanged(Eq.fromUniversalEquals)
  override def drop(num: Int): Observable[A] = stream.drop(num)
  override def dropWhile(predicate: A => Boolean): Observable[A] = stream.dropWhile(predicate)
  override def evalMap[B](fn: A => Task[B]): Observable[B] = stream.mapEval(fn)
  override def filter(fn: A => Boolean): Observable[A] = stream.filter(fn)
  override def flatMap[B](fn: A => Observable[B]): Observable[B] = stream.flatMap(fn)
  override def head: Observable[A] = stream.head
  override def map[B](fn: A => B): Observable[B] = stream.map(fn)
  override def mapAccumulate[S](seed: S)(fn: (S, A) => S): Observable[(Option[A], S)] = stream.scan[(Option[A], S)](None -> seed) { case ((_, state), next) => Some(next) -> fn(state, next) }
  override def mapAccumulate0[S](seed: S)(fn: (S, A) => S): Observable[(Option[A], S)] = stream.scan0[(Option[A], S)](None -> seed) { case ((_, state), next) => Some(next) -> fn(state, next) }
  override def mapEither[B](fn: A => Either[Throwable, B]): Observable[B] = stream.mapEval(value => Task.fromEither[Throwable, B](fn(value)))
  override def mapFilter[B](fn: A => Option[B]): Observable[B] = stream.map(fn).collect { case Some(value) => value }
  override def mergeMap[B](fn: A => Observable[B]): Observable[B] = stream.mergeMap(fn)
  override def prepend(value: A): Observable[A] = stream.prepend(value)
  override def prependAsync(value: Task[A]): Observable[A] = ???
  override def prependFuture(value: Future[A]): Observable[A] = ???
  override def recover(fn: PartialFunction[Throwable, A]): Observable[A] = ???
  override def recoverOption(fn: PartialFunction[Throwable, Option[A]]): Observable[A] = ???
  override def scan[S](seed: S)(fn: (S, A) => S): Observable[S] = ???
  override def scan0[S](seed: S)(fn: (S, A) => S): Observable[S] = ???
  override def sample(duration: FiniteDuration): Observable[A] = ???
  override def sampleMillis(millis: Int): Observable[A] = ???
  override def startsWith(values: Iterable[A]): Observable[A] = ???
  override def switchMap[B](fn: A => Observable[B]): Observable[B] = ???
  override def take(num: Int): Observable[A] = ???
  override def takeWhile(predicate: A => Boolean): Observable[A] = ???
  override def withLatest[B](latest: Observable[B]): Observable[(A, B)] = ???
  override def withLatestMap[B, R](latest: Observable[B])(fn: (A, B) => R): Observable[R] = ???
  override def zip[B](other: Observable[B]): Observable[(A, B)] = ???
  override def zipWithIndex: Observable[(A, Int)] = ???
}

class MonixBuildTx extends BuildTx {
  override type H[T] = Observable[T]

  override def combineLatest[A, B](a: Observable[A], b: Observable[B]): Observable[(A, B)] = Observable.combineLatest2(a, b)

  override def combineLatestMap[A, B, R](a: Observable[A], b: Observable[B])(fn: (A, B) => R): Observable[R] = Observable.combineLatestMap2(a, b)(fn)

  override def withLatest[S, A](source: Observable[S], latestA: Observable[A]): Observable[(S, A)] = source.withLatestFrom(latestA)((s, a) => (s, a))
}
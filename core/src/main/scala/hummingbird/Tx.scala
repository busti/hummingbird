package hummingbird

import cats.Eq

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration.FiniteDuration

trait TxBase[A] {
  type F[_]
  type EffectT[FF[_]] = TransformK[FF, F]

  type H[_]
  type StreamT[HH[_]] = TransformK[HH, H]

  private[hummingbird] val stream: H[A]

  def async: H[A]
  // def behavior(value: A)
  // def behaviorSelector
  def collect[B](fn: PartialFunction[A, B]): H[B]
  def combineLatest[B](other: H[B]): H[(A, B)]
  def combineLatestMap[B, R](other: H[B])(fn: (A, B) => R): H[R]
  def concatMapAsync[B](fn: A => F[B]): H[B]
  def concatMapFuture[B](fn: A => Future[B])(implicit ec: ExecutionContext): H[B]
  def debounce(duration: FiniteDuration): H[A]
  def debounceMillis(millis: Int): H[A]
  def delay(duration: FiniteDuration): H[A]
  def delayMillis(millis: Int): H[A]
  def distinct(implicit eq: Eq[A]): H[A]
  def distinctOnEquals: H[A]
  // def doOnError
  // def doOnNext
  // def doOnSubscribe
  def drop(num: Int): H[A]
  // def dropUntil
  def dropWhile(predicate: A => Boolean): H[A]
  def evalMap[B](fn: A => F[B]): H[B]
  def filter(fn: A => Boolean): H[A]
  def flatMap[B](fn: A => H[B]): H[B]
  // def foreach(f: A => Unit)
  def head: H[A]
  def map[B](fn: A => B): H[B]
  def mapAccumulate[S](seed: S)(fn: (S, A) => S): H[(Option[A], S)]
  def mapAccumulate0[S](seed: S)(fn: (S, A) => S): H[(Option[A], S)]
  def mapEither[B](fn: A => Either[Throwable, B]): H[B]
  def mapFilter[B](fn: A => Option[B]): H[B]
  // def mapSync
  def mergeMap[B](fn: A => H[B]): H[B]
  def prepend(value: A): H[A]
  def prependAsync(value: F[A]): H[A]
  def prependFuture(value: Future[A]): H[A]
  // def prependSync(value: F[A]): H[A]
  // def publish
  // def publishSelector
  def recover(fn: PartialFunction[Throwable, A]): H[A]
  def recoverOption(fn: PartialFunction[Throwable, Option[A]]): H[A]
  // def replay
  // def replaySelector
  def scan[S](seed: S)(fn: (S, A) => S): H[S]
  def scan0[S](seed: S)(fn: (S, A) => S): H[S]
  def sample(duration: FiniteDuration): H[A]
  def sampleMillis(millis: Int): H[A]
  def startsWith(values: Iterable[A]): H[A]
  def switchMap[B](fn: A => H[B]): H[B]
  def take(num: Int): H[A]
  def takeWhile(predicate: A => Boolean): H[A]
  // def takeUntil
  // def transformSink
  // def transformSource
  // def withDefaultSubscription
  def withLatest[B](latest: H[B]): H[(A, B)]
  def withLatestMap[B, R](latest: H[B])(fn: (A, B) => R): H[R]
  def zip[B](other: H[B]): H[(A, B)]
  def zipWithIndex: H[(A, Long)]
}

trait Tx[A] extends TxBase[A] {
  def flatMap[HB[_] : StreamT, B](fn: A => HB[B]): H[B] = flatMap[B](value => TransformK[HB, H].transform(fn(value)))
  def evalMap[FF[_] : EffectT, B](fn: A => FF[B]): H[B] = evalMap[B](value => TransformK[FF, F].transform(fn(value)))
}

trait BuildTx {
  type H[_]
  type StreamT[HH[_]] = TransformK[HH, H]

  def combineLatest[A, B](a: H[A], b: H[B]): H[(A, B)]

  def combineLatest[HA[_] : StreamT, HB[_] : StreamT, A, B](ha: HA[A], hb: HB[B]): H[(A, B)] =
    combineLatest(
      TransformK[HA, H].transform(ha),
      TransformK[HB, H].transform(hb)
    )

  def combineLatestMap[A, B, R](a: H[A], b: H[B])(fn: (A, B) => R): H[R]

  def combineLatestMap[HA[_] : StreamT, HB[_] : StreamT, A, B, R](ha: HA[A], hb: HB[B])(fn: (A, B) => R): H[R] =
    combineLatestMap(
      TransformK[HA, H].transform(ha),
      TransformK[HB, H].transform(hb)
    )(fn)

  def withLatest[S, A](source: H[S], latestA: H[A]): H[(S, A)]

  def withLatest[HS[_] : StreamT, HA[_] : StreamT, S, A](source: HS[S], latestA: HA[A]): H[(S, A)] =
    withLatest(
      TransformK[HS, H].transform(source),
      TransformK[HA, H].transform(latestA)
    )
}
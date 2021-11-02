package hummingbird.interop.monix

import hummingbird._
import monix.eval._
import monix.reactive._

class MonixTx[+A](val stream: Observable[A]) extends Tx[A] {
  type F[+T] = Task[T]
  type H[+T] = Observable[T]
  type I[-T] = MonixRx[T] { type I[-X] = MonixRx[X] }
  type J[+T] = MonixTx[T]

  def map[B](f: A => B): J[B] = new MonixTx(stream.map(f))

  def flatMap[B](f: A => J[B]): J[B] = new MonixTx(stream.flatMap(a => f(a).stream))

  def evalMap[B](fn: A => Task[B]): J[B] = new MonixTx(stream.mapEval(fn))

  def collect[B](pf: PartialFunction[A, B]): J[B] = new MonixTx(stream.collect(pf))

  def filter(p: A => Boolean): J[A] = new MonixTx(stream.filter(p))

}

class MonixTxBuilder extends TxBuilder {
  type H[+T] = Observable[T]
  type J[+T] = MonixTx[T]

  def empty[A]: MonixTx[A] = new MonixTx(Observable.empty)
}
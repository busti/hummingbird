package hummingbird.interop.zio.syntax

import hummingbird.syntax.Rx
import zio._
import zio.stream._

class ZioRx[-A](val stream: Sink[Nothing, A, Nothing, Nothing]) extends Rx[A]{
  type F[+T] = Task[T]
  type G[+T] = Sink[Nothing, T, Nothing, Nothing]
  type I[-T] = ZioRx[T]
  type J[+T] = ZioTx[T]

  override def contraMap[B](f: B => A): ZioRx[B] = ???

  override def redirect[B](transform: ZioTx[_ <: B] => ZioTx[A]): ZioRx[B] = ???
}

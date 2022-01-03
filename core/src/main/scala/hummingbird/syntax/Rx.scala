package hummingbird.syntax

import hummingbird.TransformK

trait Rx[-A] { self =>
  type F[+_]
  type EffectT[FF[_]] = TransformK[FF, F]

  type G[-_]
  type StreamT[GG[_]] = TransformK[GG, G]

  //noinspection DuplicatedCode
  type I[-T] <: Rx[T] { type I[-X] = self.I[X]; type J[+X] = self.J[X] }
  //noinspection DuplicatedCode
  type J[+T] <: Tx[T] { type I[-X] = self.I[X]; type J[+X] = self.J[X] }

  private[hummingbird] val stream: G[A]

  def contraMap[B](fn: B => A): I[B]

  def redirect[B](transform: J[_ <: B] => J[A]): I[B]
}

trait RxBuilder { self =>
  type G[-_]
  type StreamT[GG[_]] = TransformK[GG, G]

  type I[-T] <: Rx[T] { type G[-X] <: self.G[X]; type I[-X] = self.I[X] }

  def empty[A]: I[A]

  def foreach[A](consume: A => Unit): I[A]
}
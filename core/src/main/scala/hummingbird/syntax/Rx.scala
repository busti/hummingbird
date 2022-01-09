package hummingbird.syntax

import hummingbird.{Context, TransformK}

trait Rx extends Context { self =>
  def contraMap[A, B](sink: G[A])(f: B => A): G[B]

  def redirect[A, B](sink: G[A])(transform: H[B] => H[A]): G[B]
}

trait RxBuilder { self =>
  type G[-_]
  type StreamT[GG[_]] = TransformK[GG, G]

  type I[-T] <: Rx { type G[-X] <: self.G[X]; type I[-X] = self.I[X] }

  def empty[A]: I[A]

  def foreach[A](consume: A => Unit): I[A]
}
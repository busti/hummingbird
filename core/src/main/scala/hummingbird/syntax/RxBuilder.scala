package hummingbird.syntax

import hummingbird.Context

trait RxBuilder[GG[-_]] extends Context {
  type G[T] = GG[T]

  def empty[A]: G[A]

  def foreach[A](consume: A => Unit): G[A]
}
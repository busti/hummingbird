package hummingbird.syntax

import hummingbird.Context

trait Rx extends Context {
  def contraMap[A, B](sink: G[A])(f: B => A): G[B]

  def redirect[A, B](sink: G[A])(transform: H[B] => H[A]): G[B]
}
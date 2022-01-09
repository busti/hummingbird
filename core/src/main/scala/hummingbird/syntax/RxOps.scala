package hummingbird.syntax

import hummingbird.Context

class RxOps[GG[_], A](val sink: GG[A]) extends AnyVal with Context { self =>
  type G[-T] = GG[T]
  type RRx <: Rx {
    type Cancelable = self.Cancelable
    type F[+T] = self.F[T]
    type G[-T] = self.G[T]
    type H[+T] = self.H[T]
  }

  def contraMap[B](f: B => A)(implicit rx: RRx): G[B] = rx.contraMap(sink)(f)

  def redirect[B](transform: H[B] => H[A])(implicit rx: RRx): G[B] = rx.redirect(sink)(transform)
}
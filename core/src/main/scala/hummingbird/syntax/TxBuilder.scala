package hummingbird.syntax

import hummingbird.Context

trait TxBuilder[GG[_]] extends Context {
  type G[A] = GG[A]
}

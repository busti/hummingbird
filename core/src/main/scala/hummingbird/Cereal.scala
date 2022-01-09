package hummingbird

import syntax._
import scala.language.implicitConversions

trait Cereal extends Any with Context { self =>
  type Rx[-T] = G[T]
  implicit def iRx: syntax.Rx
  implicit def rxOps[T](source: G[T]): RxOps[G, T] = new RxOps[G, T](source)

  type Tx[+T] = H[T]
  implicit def iTx: syntax.Tx
  implicit def txOps[T](source: H[T]): TxOps[H, T] = new TxOps[H, T](source)
}

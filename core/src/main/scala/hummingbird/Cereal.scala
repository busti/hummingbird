package hummingbird

import syntax._
import scala.language.implicitConversions

trait Cereal extends Any with Context { self =>
  type Rx[-T] = G[T]
  def Rx: RxBuilder[Rx]
  implicit def iRx: syntax.Rx
  implicit def rxOps[T](source: Rx[T]): RxOps[G, T] = new RxOps[G, T](source)

  type Tx[+T] = H[T]
  def Tx: TxBuilder[Tx]
  implicit def iTx: syntax.Tx
  implicit def txOps[T](source: Tx[T]): TxOps[H, T] = new TxOps[H, T](source)
}

package hummingbird

import hummingbird.syntax.{RxBuilder, TxBuilder}

trait Cereal {
  val Rx: RxBuilder
  type Rx[-T] = Rx.I[T]

  val Tx: TxBuilder
  type Tx[+T] = Tx.J[T]
}

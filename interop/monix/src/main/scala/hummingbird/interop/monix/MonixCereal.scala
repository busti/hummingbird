package hummingbird.interop.monix

import hummingbird._
import hummingbird.interop.monix.syntax.{MonixRxBuilder, MonixTxBuilder}
import hummingbird.syntax.{RxBuilder, TxBuilder}
import monix.execution.Scheduler

class MonixCereal(implicit scheduler: Scheduler) extends Cereal {
  val Rx: RxBuilder = new MonixRxBuilder
  val Tx: TxBuilder = new MonixTxBuilder
}

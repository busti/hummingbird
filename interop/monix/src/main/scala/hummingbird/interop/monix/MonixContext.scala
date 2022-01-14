package hummingbird.interop.monix

import hummingbird.Context
import monix.eval._
import monix.reactive._

trait MonixContext extends Context {
  type Cancelable = monix.execution.Cancelable
  type F[+T] = Task[T]
  type G[-T] = Observer[T]
  type H[+T] = Observable[T]
}

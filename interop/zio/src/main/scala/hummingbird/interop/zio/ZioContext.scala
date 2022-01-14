package hummingbird.interop.zio

import hummingbird.Context
import zio._
import zio.stream._

trait ZioContext[-R, +E] extends Context {
  type Cancelable = Unit => UIO[Unit]
  type F[+T] = ZIO[R, E, T]
  type G[-T] = ZSink[R, E, T, Unit, Unit]
  type H[+T] = ZStream[R, E, T]
}

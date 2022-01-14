package hummingbird.interop.fs2

import hummingbird.Context
import fs2._

trait Fs2Context[FF[_]] extends Context {
  type Cancelable = Any
  type F[+T] = FF[T]
  type G[-T] = Pipe[F, T, Unit]
  type H[+T] = Stream[F, T]
}

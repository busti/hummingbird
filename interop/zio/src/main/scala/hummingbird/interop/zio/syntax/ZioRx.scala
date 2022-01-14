package hummingbird.interop.zio.syntax

import hummingbird.interop.zio.ZioContext
import hummingbird.syntax.Rx
import zio._
import zio.stream._

class ZioRx[R, E] extends Rx with ZioContext[R, E] {
  def contraMap[A, B](sink: ZSink[R, E, A, Unit, Unit])(f: B => A): ZSink[R, E, B, Unit, Unit] =
    sink.contramap(f)

  def redirect[A, B](sink: G[A])(transform: H[B] => H[A]): G[B] =
    ZSink.foreachChunk( chunk =>
      transform(
        ZStream.asyncZIO(emit => UIO(emit.chunk(chunk)))
      ).run(sink)
    )
}
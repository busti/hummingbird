package hummingbird.interop.fs2.syntax

import hummingbird.interop.fs2.Fs2Context
import hummingbird.syntax.Rx

class Fs2Rx[F[_]] extends Rx with Fs2Context[F] {

}

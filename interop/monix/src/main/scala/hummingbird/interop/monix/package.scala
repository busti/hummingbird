package hummingbird.interop

import _root_.hummingbird.TransformK
import _root_.monix.eval._

package object monix {
  implicit def transformTaskF[F[_] : TaskLike]: TransformK[F, Task] = new TransformK[F, Task] {
    override def transform[A](xa: F[A]): Task[A] = Task.from(xa)
  }
}

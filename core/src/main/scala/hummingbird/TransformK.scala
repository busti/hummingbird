package hummingbird

trait TransformK[X[_], Y[_]] {
  def transform[A](xa: X[A]): Y[A]
}

object TransformK {
  def apply[X[_], Y[_]](implicit instance: TransformK[X, Y]): TransformK[X, Y] = instance

  def id[F[_]]: TransformK[F, F] = new TransformK[F, F] {
    def transform[A](xa: F[A]): F[A] = xa
  }
}
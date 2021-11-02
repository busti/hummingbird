package hummingbird

trait Tx[+A] { self =>
  type F[+_]
  type EffectT[FF[_]] = TransformK[FF, F]

  type H[+_]
  type StreamT[HH[_]] = TransformK[HH, H]

  type I[-T] <: Rx[T] { type I[-X] <: self.I[X] }
  type J[+T] <: Tx[T]

  private[hummingbird] val stream: H[A]

  def map[B](fn: A => B): J[B]

  def flatMap[B](fn: A => J[B]): J[B]

  def evalMap[B](fn: A => F[B]): J[B]

  def collect[B](pf: PartialFunction[A, B]): J[B]

  def filter(p: A => Boolean): J[A]

}

trait TxBuilder { self =>
  type H[+_]
  type StreamT[HH[_]] = TransformK[HH, H]

  type J[+T] <: Tx[T] { type H[+X] <: self.H[X] }

  def empty[A]: J[A]
}
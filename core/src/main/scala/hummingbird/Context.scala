package hummingbird

trait Context extends Any {
  type Cancelable

  type F[+T] // Effect
  type G[-T] // Rx
  type H[+T] // Tx
}

package hummingbird.interop.monix.syntax

import hummingbird.interop.monix.MonixContext
import hummingbird.syntax.Rx
import monix.execution.Scheduler
import monix.reactive.{Observable, Observer}
import monix.reactive.subjects.PublishSubject

class MonixRx(implicit scheduler: Scheduler) extends Rx with MonixContext {
  def contraMap[A, B](sink: Observer[A])(f: B => A): Observer[B] = sink.contramap(f)

  def redirect[A, B](sink: Observer[A])(transform: Observable[B] => Observable[A]): Observer[B] = {
    val handler = PublishSubject[B]
    val source = transform(handler)
    source.subscribe(sink)
    handler
  }
}
package hummingbird.interop.monix.syntax

import hummingbird.syntax.{Rx, RxBuilder}
import monix.eval.Task
import monix.execution.{Ack, Scheduler, UncaughtExceptionReporter}
import monix.reactive.Observer
import monix.reactive.observers.{SafeSubscriber, Subscriber}
import monix.reactive.subjects.PublishSubject

import scala.concurrent.Future

class MonixRx[-A](val stream: Observer[A])(implicit scheduler: Scheduler) extends Rx[A] { self =>
  type F[+T] = Task[T]
  type G[-T] = Observer[T]
  type I[-T] = MonixRx[T]
  type J[+T] = MonixTx[T]

  def contraMap[B](fn: B => A): I[B] = new MonixRx(stream.contramap(fn))

  def redirect[B](transform: J[B] => J[A]): I[B] = {
    val handler = PublishSubject[B]
    val source = transform(new MonixTx(handler)).stream
    source.subscribe(stream)
    new MonixRx(handler)
  }
}

class MonixRxBuilder(implicit scheduler: Scheduler) extends RxBuilder { self =>
  type G[-T] = Observer[T]
  type I[-T] = MonixRx[T]

  def empty[A]: MonixRx[A] = new MonixRx(Observer.empty)

  def foreach[A](fn: A => Unit): I[A] = new MonixRx(SafeSubscriber(new Subscriber[A] {
    def scheduler: Scheduler = self.scheduler
    override def onNext(elem: A): Future[Ack] = { fn(elem); Ack.Continue }
    override def onError(ex: Throwable): Unit = UncaughtExceptionReporter.default.reportFailure(ex)
    override def onComplete(): Unit = ()
  }))
}
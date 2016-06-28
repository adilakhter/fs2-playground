package io.untyped.fs2playground

object C_IntegerStream extends App {
  import fs2._
  import fs2.util._
  import Stream._

  implicit val scheduler: Scheduler = Scheduler.fromFixedDaemonPool(8)
  implicit val strategy: Strategy = Strategy.fromFixedDaemonPool(8)


  def integerStream: Stream[Task, Int] = {
    def next (i: Int) : Stream[Task, Int] = eval(Task(i)).flatMap { i ⇒
      emit(i) ++ next(i + 1)
    }
    next(1)
  }

  def snk[A]: Sink[Task, A] =
    _.evalMap{ a ⇒
      Task.delay{
        println(s"> $a")
      }
    }


  integerStream.take(10).to(snk).run.unsafeRun
}

import akka.NotUsed
import akka.stream.OverflowStrategy
import akka.stream.QueueOfferResult.Enqueued
import akka.stream.scaladsl.{Flow, Keep, Sink, Source, SourceQueueWithComplete}

import scala.concurrent.{ExecutionContext, Promise}
import scala.util.{Left, Right}

object AggregateStage {
  def apply[E, O](zero: O)(agg: (O, E) => O)(implicit ec: ExecutionContext): Sink[(Int, E), Later[O]] = {
    val states: Flow[(Int, E), Right[Nothing, (Int, O)], NotUsed] =
      Flow[(Int, E)]
        .scan(0 -> zero) { (state, e) =>
          e._1 -> agg(state._2, e._2)
        }
        .map(Right(_))

    val request: Source[Left[(Int, Promise[O]), Nothing], SourceQueueWithComplete[(Int, Promise[O])]] = Source
      .queue[(Int, Promise[O])](10, OverflowStrategy.dropNew)
      .map(Left(_))

    val r: Flow[(Int, E), Either[(Int, Promise[O]), (Int, O)], SourceQueueWithComplete[(Int, Promise[O])]] =
      states.mergeMat(request)(Keep.right)

    val respond = r.toMat(Sink.fold((0, zero, List.empty[(Int, Promise[O])])) { (le, e) =>
      val (lastToken, lastState, l) = le

      e match {
        case Left((token, promis)) if lastToken >= token =>
          promis.success(lastState)
          (lastToken, lastState, l)
        case Left(value) =>
          (lastToken, lastState, value :: l)
        case Right((token, state)) =>
          val (resolved, unresolved) =
            l.partition(_._1 <= token)
          resolved.foreach(_._2.success(state))

          (token, state, unresolved)
      }
    })(Keep.left)

    respond.mapMaterializedValue { (queue: SourceQueueWithComplete[(Int, Promise[O])]) =>
      Later.await { token =>
        val prom = Promise[O]
        queue
          .offer(token -> prom)
          .flatMap { case Enqueued => prom.future }
          .map(token -> _)
      }
    }
  }
}

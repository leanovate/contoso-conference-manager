package dao

import akka.NotUsed
import akka.stream.{Materializer, OverflowStrategy, QueueOfferResult}
import akka.stream.scaladsl.{Flow, Keep, Sink, Source, SourceQueueWithComplete}
import org.reactivestreams.Publisher
import play.api.libs.json.{Format, JsSuccess, Json}

import scala.concurrent.Future

abstract class EventDao[Event](
    implicit val mat: Materializer,
    format: Format[Event]) {

  def store(str: Event): Future[QueueOfferResult] =
    in.offer(str)

  val (
    in: SourceQueueWithComplete[Event],
    out: Publisher[Event]
  ) =
    Source
      .queue[Event](3, OverflowStrategy.backpressure)
      .map(Json.toJson(_))
      .map(Json.stringify)
      .via(eventStore)
      .map(Json.parse)
      .map(Json.fromJson[Event])
      .collect { case JsSuccess(event, _) => event }
      .toMat(Sink.asPublisher(fanout = true))(Keep.both)
      .run()

  protected def eventStore: Flow[String, String, NotUsed]
}

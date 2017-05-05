import java.util.concurrent.atomic.AtomicInteger

import akka.actor.ActorSystem
import akka.stream.QueueOfferResult.Enqueued
import akka.stream.scaladsl.{Keep, Sink, Source}
import akka.stream.{ActorMaterializer, OverflowStrategy}

import cats.syntax.all._

import scala.concurrent.duration._

object Sys extends App {

  implicit val sys = ActorSystem("Test")
  implicit val ec  = sys.dispatcher
  implicit val mat = ActorMaterializer()

  object Client extends App {


    val res = for {
      _   <- EventStore.process("Hello")
      _   <- EventStore.process("World")
      agg <- aggregate
    } yield s"Received: $agg"

    Later.run(res).foreach(println)
  }

  object EventStore {
    type Data  = String
    type Token = Int

    private val lastToken = new AtomicInteger()

    val (in, out) =
      Source
        .queue[(Token, Data)](10, OverflowStrategy.dropNew)
        .toMat(Sink.asPublisher(fanout = true))(Keep.both)
        .run()

    def process(data: Data): Later[Unit] = Later.await{ _: Int =>
      val token = lastToken.incrementAndGet()

      in.offer(token -> data).collect {
        case Enqueued =>
          token -> ()
      }
    }
  }

  val aggregate: Later[String] =
    Source
      .fromPublisher(EventStore.out)
      .delay(300.milli)
      .runWith(AggregateStage("")((prev, e) => prev + " " + e))

  EventStore.process("wwww")
  Client.main(args)
  Thread.sleep(1000)
  Later.run(aggregate).foreach(state => println("Final state:" + state))
  sys.terminate()
}

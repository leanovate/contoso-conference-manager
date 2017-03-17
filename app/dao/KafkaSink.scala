package dao

import akka.actor.ActorSystem
import akka.kafka.ProducerSettings
import akka.kafka.scaladsl.Producer
import akka.stream.scaladsl.{Source, SourceQueueWithComplete}
import akka.stream.{ActorMaterializer, OverflowStrategy}
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.{ByteArraySerializer, StringSerializer}

class KafkaSink {
  implicit val system = ActorSystem("kafka")
  implicit val materializer = ActorMaterializer()
  val producerSettings = ProducerSettings(system, new ByteArraySerializer(), new StringSerializer())
    .withBootstrapServers("localhost:9092")

  val done: SourceQueueWithComplete[String] = Source.queue[String](3, OverflowStrategy.backpressure)
    .map { elem =>
      new ProducerRecord[Array[Byte], String]("helloWorldTopic", elem)
    }.to(Producer.plainSink(producerSettings)).run()


  def apply(str: String) =
    done.offer(str)
}
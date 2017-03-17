package dao

import akka.actor.ActorSystem
import akka.kafka.scaladsl.Consumer.Control
import akka.kafka.scaladsl.{Consumer, Producer}
import akka.kafka.{ConsumerSettings, ProducerSettings, Subscriptions}
import akka.stream.scaladsl.{Sink, Source, SourceQueueWithComplete}
import akka.stream.{ActorMaterializer, OverflowStrategy}
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.{ByteArrayDeserializer, ByteArraySerializer, StringDeserializer, StringSerializer}
import org.reactivestreams.Publisher

trait EventDao {

  val in: SourceQueueWithComplete[String]

  val out: Publisher[String]


  def apply(str: String) =
    in.offer(str)
}

class KafkaDao extends EventDao {
  implicit val system = ActorSystem("kafka")
  implicit val materializer = ActorMaterializer()
  val producerSettings = ProducerSettings(system, new ByteArraySerializer(), new StringSerializer())
    .withBootstrapServers("localhost:9092")

  val in: SourceQueueWithComplete[String] = Source.queue[String](3, OverflowStrategy.backpressure)
    .map { elem =>
      new ProducerRecord[Array[Byte], String]("helloWorldTopic", elem)
    }.to(Producer.plainSink(producerSettings)).run()



  private val consumerSettings = ConsumerSettings(system, new ByteArrayDeserializer, new StringDeserializer)
    .withBootstrapServers("localhost:9092")
    .withGroupId("group1")

  val out: Publisher[String] =
    Consumer
      .atMostOnceSource(consumerSettings, Subscriptions.topics("helloWorldTopic"))
      .map(_.value())
      .runWith(Sink.asPublisher(fanout = true))
}
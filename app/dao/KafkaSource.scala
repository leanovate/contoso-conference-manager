package dao

import akka.actor.ActorSystem
import akka.kafka.scaladsl.Consumer
import akka.kafka.{ConsumerSettings, Subscriptions}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Sink
import org.apache.kafka.common.serialization.{ByteArrayDeserializer, StringDeserializer}

class KafkaSource {
  implicit val system = ActorSystem("kafka")
  implicit val materializer = ActorMaterializer()
  private val consumerSettings = ConsumerSettings(system, new ByteArrayDeserializer, new StringDeserializer)
    .withBootstrapServers("localhost:9092")
    .withGroupId("group1")

  val done = Consumer.atMostOnceSource(consumerSettings, Subscriptions.topics("helloWorldTopic"))
    .map(println)
    .runWith(Sink.ignore)
}

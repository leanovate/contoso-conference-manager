package dao

import akka.NotUsed
import akka.actor.ActorSystem
import akka.kafka.scaladsl.Consumer.Control
import akka.kafka.scaladsl.{Consumer, Producer}
import akka.kafka.{
  ConsumerSettings,
  ProducerSettings,
  Subscriptions
}
import akka.stream.Materializer
import akka.stream.scaladsl.{Flow, Sink, Source}
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.{
  ByteArrayDeserializer,
  ByteArraySerializer,
  StringDeserializer,
  StringSerializer
}
import play.api.libs.json.Format
import com.softwaremill.tagging.@@

class KafkaDao[Event](val topic: String @@ Event)(implicit mat: Materializer,
                      sys: ActorSystem,
                      format: Format[Event])
    extends EventDao[Event] {

  def producerSettings =
    ProducerSettings(sys,
                     new ByteArraySerializer(),
                     new StringSerializer())
      .withBootstrapServers("localhost:9092")

  private def kafkaIn: Sink[String, NotUsed] =
    Flow[String]
      .map { elem =>
        new ProducerRecord[Array[Byte], String](topic,
                                                elem)
      }
      .to(Producer.plainSink(producerSettings))

  private def consumerSettings =
    ConsumerSettings(sys,
                     new ByteArrayDeserializer,
                     new StringDeserializer)
      .withBootstrapServers("localhost:9092")
      .withGroupId("group1")

  private def kafkaOut: Source[String, Control] =
    Consumer
      .plainSource(consumerSettings,
                   Subscriptions.topics(topic))
      .map(_.value())

  override protected def eventStore
    : Flow[String, String, NotUsed] =
    Flow.fromSinkAndSource(kafkaIn, kafkaOut)
}

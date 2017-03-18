package dao

import akka.actor.ActorSystem
import models.{OrderPlaced, UserRecognized}
import play.api.BuiltInComponents

import scala.concurrent.ExecutionContext

trait DaoModule extends BuiltInComponents {

  import com.softwaremill.macwire.wire
  import com.softwaremill.tagging._

  implicit def ec: ExecutionContext =
    ExecutionContext.global
  implicit def sys: ActorSystem =
    actorSystem

  private def userTopic =
    "users".taggedWith[UserRecognized]
  private def orderTopic = "orders".taggedWith[OrderPlaced]

  lazy val orderDao: EventDao[OrderPlaced] =
    wire[KafkaDao[OrderPlaced]]
  lazy val userDao: EventDao[UserRecognized] =
    wire[KafkaDao[UserRecognized]]
}

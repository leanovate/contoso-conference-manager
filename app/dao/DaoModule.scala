package dao

import models.{OrderPlaced, UserRecognized}
import play.api.BuiltInComponents

import scala.concurrent.ExecutionContext

trait DaoModule extends BuiltInComponents {

  import com.softwaremill.macwire._

  lazy val ec: ExecutionContext = ExecutionContext.global

  private def userTopic = "users".taggedWith[UserRecognized]
  private def orderTopic = "orders".taggedWith[OrderPlaced]

  lazy val orderDao: EventDao[OrderPlaced] =
    wire[KafkaDao[OrderPlaced]]
  lazy val userDao: EventDao[UserRecognized] =
    wire[KafkaDao[UserRecognized]]
}

package services

import dao.EventDao
import models.{Order, OrderPlaced}
import akka.stream.QueueOfferResult._

import scala.concurrent.{ExecutionContext, Future}

class OrderService(eventDao: EventDao[OrderPlaced])(
    implicit ec: ExecutionContext) {
  def saveNew(order: Order): Future[Unit] =
    eventDao
      .store(OrderPlaced(order))
      .collect {
        case Enqueued => Unit
      }
}

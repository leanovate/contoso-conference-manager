package services

import java.util.concurrent.atomic.AtomicReference

import akka.actor.ActorSystem
import akka.stream.Materializer
import akka.stream.scaladsl.Source
import dao.EventDao
import models.{Order, OrderPlaced}

class PlacedOrders(orders: EventDao[OrderPlaced])(
    implicit sys: ActorSystem,
    mat: Materializer) {

  val allOrders = new AtomicReference[Seq[Order]]

  Source
    .fromPublisher(orders.out)
    .scan(List.empty[Order]) {
      case (orders, OrderPlaced(order)) =>
        orders :+ order
    }
    .runForeach(allOrders.set)

}

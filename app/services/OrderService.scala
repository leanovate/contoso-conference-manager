package services

import dao.EventDao
import models.{Order, OrderPlaced}

class OrderService(eventDao: EventDao[OrderPlaced]) {
  def saveNew(order: Order) =
    eventDao.store(OrderPlaced(order))
}

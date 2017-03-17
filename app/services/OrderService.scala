package services

import dao.EventDao
import models.Order

class OrderService(eventDao: EventDao) {
  def saveNew(order: Order) = eventDao(order.toString)
}

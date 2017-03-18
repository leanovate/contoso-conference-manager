package services

import dao.DaoModule

trait ServicesModule extends DaoModule {

  import com.softwaremill.macwire.wire

  lazy val orderService: OrderService = wire[OrderService]
  val newUserService: NewUserService  = wire[NewUserService]
  val placedOrders: PlacedOrders      = wire[PlacedOrders]

}

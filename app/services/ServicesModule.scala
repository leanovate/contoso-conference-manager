package services

import dao.DaoModule

trait ServicesModule extends DaoModule {

  import com.softwaremill.macwire.MacwireMacros._

  lazy val orderService: OrderService = wire[OrderService]
  val newUserService: NewUserService  = wire[NewUserService]
  val placedOrders: PlacedOrders      = wire[PlacedOrders]

}

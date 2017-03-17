package services

import dao.DaoModule

trait ServicesModule extends DaoModule {

  import com.softwaremill.macwire.MacwireMacros._

  lazy val orderService = wire[OrderService]
  val newUserService = wire[NewUserService]
  val placedOrders = wire[PlacedOrders]

}

package controllers

import models.Order
import play.api.libs.json.{Json, Reads}
import play.api.mvc.{Action, Controller}
import play.twirl.api.Html
import services.OrderService

import scala.concurrent.Future

class OrderController(orderService: OrderService) extends Controller {

  val orderStore = Seq(
    Order(1, 1, 2),
    Order(2, 1, 3)
  )

  def ordersGet = Action {
    Ok(Json.toJson(orderStore))
  }

  def ordersPost = Action.async(parse.json(oneOrMayOrders)) { request =>
    val orders: List[Order] = request.body
    println(orders)
    orderService
    Future.successful(Ok)
  }

  val oneOrMayOrders: Reads[List[Order]] =
    implicitly[Reads[Order]].map(List(_)).orElse(implicitly[Reads[List[Order]]])

  def index = Action {
    Ok(Html("<h1>Welcome</h1><p>Your new application is ready.</p>"))
  }

}

package controllers

import models.Order
import play.api.libs.json.{Json, Reads}
import play.api.mvc.{Action, Controller}
import play.twirl.api.Html
import services.{OrderService, PlacedOrders}

import scala.concurrent.{ExecutionContext, Future}

class OrderController(orderService: OrderService,
                      placedOrders: PlacedOrders)(
    implicit ec: ExecutionContext)
    extends Controller {

  def ordersGet = Action {
    Ok(Json.toJson(placedOrders.allOrders.get))
  }

  def ordersPost =
    Action.async(parse.json(oneOrManyOrders)) { request =>
      val orders: List[Order] = request.body

      Future
        .traverse(orders)(orderService.saveNew)
        .map(list => Ok)
    }

  val oneOrManyOrders: Reads[List[Order]] =
    implicitly[Reads[Order]]
      .map(List(_))
      .orElse(implicitly[Reads[List[Order]]])

  def index = Action {
    Ok(Html(
      "<h1>Welcome</h1><p>Your new application is ready.</p>"))
  }

}

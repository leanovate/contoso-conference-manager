import play.api.libs.json.Json

package object models {

  case class Order(id: Int = -1, customerId: Int, seats: Int)

  object Order {
    implicit val OrderFormat = Json.format[Order]
  }

}

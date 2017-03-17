import play.api.libs.json.{Json, OFormat}

package object models {

  case class Order(id: Int = -1,
                   customerId: Int,
                   attendees: List[String])

  object Order {
    implicit val OrderFormat: OFormat[Order] =
      Json.format[Order]
  }

  case class OrderPlaced(order: Order)

  object OrderPlaced {
    implicit val OrderPlacedFormat: OFormat[OrderPlaced] =
      Json.format[OrderPlaced]
  }

  case class UserRecognized(name: String)

  object UserRecognized {
    implicit val userRecognizedFormat
      : OFormat[UserRecognized] =
      Json.format[UserRecognized]
  }

}

package services

import akka.stream.Materializer
import akka.stream.scaladsl.Source
import dao.EventDao
import models.{OrderPlaced, UserRecognized}

class NewUserService(eventDao: EventDao[OrderPlaced],
                     users: EventDao[UserRecognized])(
    implicit val mat: Materializer) {

  Source
    .fromPublisher(eventDao.out)
    .mapConcat { placing =>
      placing.order.attendees.map(UserRecognized(_))
    }
    .runForeach(users.store(_))

}

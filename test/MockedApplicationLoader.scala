import akka.NotUsed
import akka.stream.scaladsl.Flow
import dao.EventDao
import models.{OrderPlaced, UserRecognized}
import play.api.{Application, BuiltInComponentsFromContext}
import play.api.ApplicationLoader.Context
class MockedApplicationLoader
    extends ConferenceManagerApplicationLoader {
  override def load(context: Context): Application =
    new BuiltInComponentsFromContext(context)
    with ConferenceManagerComponents {
      override lazy val orderDao: EventDao[OrderPlaced] =
        new EventDao[OrderPlaced]() {
          override protected def eventStore
            : Flow[String, String, NotUsed] = Flow[String]
        }
      override lazy val userDao: EventDao[UserRecognized] =
        new EventDao[UserRecognized]() {
          override protected def eventStore
            : Flow[String, String, NotUsed] = Flow[String]
        }
    }.application
}

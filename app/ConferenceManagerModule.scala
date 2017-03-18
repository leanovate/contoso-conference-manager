import controllers.OrderController
import services.ServicesModule

trait ConferenceManagerModule extends ServicesModule {

  import com.softwaremill.macwire._

  lazy val conferenceManagerController = wire[OrderController]
}

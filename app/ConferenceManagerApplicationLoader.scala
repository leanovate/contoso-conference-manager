
import com.softwaremill.macwire._
import controllers.Assets
import play.api.ApplicationLoader.Context
import play.api._
import play.api.i18n._
import play.api.routing.Router
import router.Routes

/**
 * Application loader that wires up the application dependencies using Macwire
 */
class ConferenceManagerApplicationLoader extends ApplicationLoader {
  def load(context: Context): Application = {
    (new BuiltInComponentsFromContext(context) with ConferenceManagerComponents).application
  }
}

trait ConferenceManagerComponents extends BuiltInComponents with ConferenceManagerModule with I18nComponents {
  lazy val assets: Assets = wire[Assets]
  lazy val router: Router = {
    val prefix = "/"
    wire[Routes]
  }
}

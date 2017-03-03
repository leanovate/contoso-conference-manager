package services

import dao.DaoModule

trait ServicesModule extends DaoModule {

  import com.softwaremill.macwire.MacwireMacros._

  lazy val greetingService = wire[GreetingService]

}

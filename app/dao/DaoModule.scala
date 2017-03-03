package dao

trait DaoModule {

  import com.softwaremill.macwire.MacwireMacros._

  lazy val kafkaSink = wire[KafkaSink]
}

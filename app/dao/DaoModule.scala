package dao

trait DaoModule {

  import com.softwaremill.macwire.MacwireMacros._

  lazy val kafkaDao: EventDao = wire[KafkaDao]
}

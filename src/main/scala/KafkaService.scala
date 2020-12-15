import akka.actor.ActorSystem
import akka.kafka.ProducerSettings
import com.typesafe.config.Config
import org.apache.kafka.common.serialization.StringSerializer

import scala.concurrent.ExecutionContextExecutor

object KafkaService {


  implicit val system: ActorSystem = ActorSystem("MySystem")
  implicit val ec: ExecutionContextExecutor = system.dispatcher
  val config: Config = system.settings.config.getConfig("akka.kafka.producer")
  val server: String = system.settings.config.getString("akka.kafka.producer.kafka-clients.server")
  val producerSettings: ProducerSettings[String, String] =
    ProducerSettings(config, new StringSerializer, new StringSerializer)
      .withBootstrapServers(server)



}

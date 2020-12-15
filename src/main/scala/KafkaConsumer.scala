import java.util.concurrent.atomic.AtomicLong

import KafkaService.{ec, system}
import akka.Done
import akka.kafka.{ConsumerSettings, Subscriptions}
import akka.kafka.scaladsl.Consumer
import org.apache.kafka.clients.consumer.{ConsumerConfig, ConsumerRecord}
import org.apache.kafka.common.TopicPartition
import org.apache.kafka.common.serialization.StringDeserializer

import scala.collection.mutable
import scala.concurrent.Future

object KafkaConsumer {

  var MapOfCities: mutable.Map[String, String] = mutable.Map.empty
  def getCitiesFromKafka:Vector[CityData] = {
    var myCityVector: Vector[CityData] = Vector.empty
    class OffsetStore {
      private val offset = new AtomicLong
      def businessLogicAndStoreOffset(record: ConsumerRecord[String, String]): Future[Done] = {
        print(s"DB.save: ${record.value} ")
        MapOfCities += (record.key -> record.value)
        Future.successful(Done)
      }

      def loadOffset(): Future[Long] = {
        Future.successful(offset.get)
      }
    }
    val bootstrapServers: String =
      system.settings.config.getString("akka.kafka.producer.kafka-clients.server")

    val consumerSettings: ConsumerSettings[String, String] =
      ConsumerSettings(system, new StringDeserializer, new StringDeserializer)
        .withBootstrapServers(bootstrapServers)
        .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest")
        .withClientId("externalOffsetStorage")

    val db = new OffsetStore
    db.loadOffset().map { fromOffset =>
      Consumer
        .plainSource(
          consumerSettings,
          Subscriptions.assignmentWithOffset(
            new TopicPartition("topic_1", 0) -> fromOffset,
          )
        )
        .mapAsync(1)(db.businessLogicAndStoreOffset).run()
    }
    MapOfCities.foreach(value => myCityVector :+= CityData(value._1, value._2))
    myCityVector
  }
}

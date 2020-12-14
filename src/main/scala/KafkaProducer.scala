import java.util.concurrent._

import KafkaService.{ec, producerSettings, system}
import akka.Done
import akka.kafka.ProducerMessage
import akka.stream.scaladsl.Source
import org.apache.kafka.clients.producer.ProducerRecord
import akka.kafka.scaladsl.Producer

import scala.concurrent.Future
import scala.util.{Failure, Success}

object KafkaProducer {
  val scheduleTime = new ScheduledThreadPoolExecutor(1)
  val task = new Runnable {
    override def run(): Unit = {
      val tempVector = HttpService.getCities(KafkaConsumer.getCitiesFromKafka)
      tempVector.foreach(city => putCityToKafka(city))
    }
  }

  def putCityToKafka(city: CityData): Unit = {
    val myCityVector: Vector[CityData] = Vector(city)
    val done2: Future[Done] =
      Source.single(myCityVector)
        .map { _ =>
          ProducerMessage.single(
            new ProducerRecord[String, String]("topic_1", city.name, city.temperature)
          )
        }
        .via(Producer.flexiFlow(producerSettings))
        .run()

    done2.onComplete {
      case Success(value) =>
        println(value)

      case Failure(exception) =>
        println(exception)

    }
  }

  val RequestPeriodicallyToKafka = scheduleTime.
    scheduleAtFixedRate(task, 1, 20, TimeUnit.SECONDS)
}

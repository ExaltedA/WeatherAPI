import java.util.concurrent.{ScheduledThreadPoolExecutor, TimeUnit}
import KafkaProducer.putCityToKafka
import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import org.slf4j.{Logger, LoggerFactory}
import scala.util.Try

object Core extends App {

  implicit val log: Logger = LoggerFactory.getLogger(getClass)

  val rootBehavior = Behaviors.setup[Nothing] { context =>

    val cityRepository = new InMemoryCityRepository()(context.executionContext)
    val router = new MyRouter(cityRepository)(context.system, context.executionContext)

    val host = "0.0.0.0"
    val port = Try(System.getenv("PORT")).map(_.toInt).getOrElse(9000)

    Server.startHttpServer(router.route, host, port)(context.system, context.executionContext)

    val scheduleTime = new ScheduledThreadPoolExecutor(1)
    val task = new Runnable {
      override def run(): Unit = {
        val tempVector = HttpService.getCities(KafkaConsumer.getCitiesFromKafka)
        tempVector.foreach(city => putCityToKafka(city))
      }
    }
    val RequestPeriodicallyToKafka = scheduleTime.
      scheduleAtFixedRate(task, 1, 20, TimeUnit.SECONDS)

    Behaviors.empty

  }

  val system = ActorSystem[Nothing](rootBehavior, "WeatherAPIServer")

}



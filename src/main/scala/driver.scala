import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import org.slf4j.{Logger, LoggerFactory}

import scala.util.Try

object driver extends App {
  implicit val log: Logger = LoggerFactory.getLogger(getClass)
  val rootBehavior = Behaviors.setup[Nothing] { context =>

    val cities: Seq[CityData] = Seq(
      CityData("1", "Nur-Sultan", "+10"),
      CityData("2", "Paris", "-5")
    )

    val cityRepository = new InMemoryCityRepository(cities)(context.executionContext)
    val router = new MyRouter(cityRepository)(context.system, context.executionContext)

    val host = "0.0.0.0"
    val port = Try(System.getenv("PORT")).map(_.toInt).getOrElse(9000)

    Server.startHttpServer(router.route, host, port)(context.system, context.executionContext)
    Behaviors.empty
  }
  val system = ActorSystem[Nothing](rootBehavior, "WeatherAPIServer")
}

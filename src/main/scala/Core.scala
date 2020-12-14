import akka.actor.typed.{ActorRef, ActorSystem, Behavior}
import akka.actor.typed.scaladsl.Behaviors
import org.slf4j.{Logger, LoggerFactory}

import scala.util.Try

object Core extends App {

  implicit val log: Logger = LoggerFactory.getLogger(getClass)

  val rootBehavior = Behaviors.setup[Nothing] { context =>

      val openApi = new OpenMap(context.executionContext)

      val cityRepository = new InMemoryCityRepository(openApi)(context.executionContext)
      val router = new MyRouter(cityRepository)(context.system, context.executionContext)

      val host = "0.0.0.0"
      val port = Try(System.getenv("PORT")).map(_.toInt).getOrElse(9000)

      Server.startHttpServer(router.route, host, port)(context.system, context.executionContext)



        Behaviors.empty

        }

  val system = ActorSystem[Nothing](rootBehavior, "WeatherAPIServer")

}



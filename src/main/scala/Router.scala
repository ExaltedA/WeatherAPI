import akka.actor.typed.ActorSystem
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import io.circe.generic.auto._
import akka.http.scaladsl.server.{Directives, Route}
import scala.concurrent.ExecutionContext

trait Router {
  def route: Route
}

class MyRouter(val cityRepository: CityRepository)(implicit system: ActorSystem[_], ex: ExecutionContext)
  extends Router
    with Directives
    with ValidatorDirectives
    with CityDirectives {

  def allCitiesRoutes = {
    pathPrefix("weatherData") {
      concat(
        pathEndOrSingleSlash {
          get {
            complete(cityRepository.all())
          }
        }
      )
    }
  }

  def cityRoute = {
    pathPrefix("weather") {
      concat(
        pathEndOrSingleSlash {
          get {
            entity(as[CreateCityData]) { createCity =>
              validateWith(CreateCityValidator)(createCity) {
                handleWithGeneric(cityRepository.select(createCity)) { city =>
                  complete(city)
                }
              }
            }
          }
        }
      )
    }
  }

  override def route = {
    concat(
      allCitiesRoutes,
      cityRoute
    )
  }
}



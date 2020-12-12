import akka.actor.typed.ActorSystem
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import  io.circe.generic.auto._
import akka.http.scaladsl.server.{Directives, Route}

import scala.concurrent.ExecutionContext

trait Router {
  def route: Route
}

class MyRouter(val cityRepository: CityRepository)(implicit system: ActorSystem[_], ex:ExecutionContext)
  extends  Router
    with  Directives
    with HealthCheckRoute
    with ValidatorDirectives
    with CityDirectives {

  def cityRoutes = {
    pathPrefix("weatherData") {
      concat(
        pathEndOrSingleSlash {
          concat(
            get {
              complete(cityRepository.all()) //TODO
            },
            post {
              entity(as[CreateCityData]) { createCity =>
                validateWith(CreateCityValidator)(createCity) {
                  handleWithGeneric(cityRepository.create(createCity)) { city =>
                    complete(city)
                  }
                }
              }
            },
            put {
              entity(as[CreateCityData]) { createCity =>
                validateWith(CreateCityValidator)(createCity) {
                  handleWithGeneric(cityRepository.put(createCity)) { city =>
                    complete(city)
                  }
                }
              }
            },
            delete {
              entity(as[CreateCityData]) { createCity =>
                validateWith(CreateCityValidator)(createCity) {
                  handleWithGeneric(cityRepository.delete(createCity)) { city =>
                    complete(city)
                  }
                }
              }
            }
          )
        }
      )
    }
  }
  def mainRoute = {
    pathPrefix("weather") {
      concat(
        pathEndOrSingleSlash {
          concat(
            get {
              entity(as[CreateCityData]) { createCity =>
                validateWith(CreateCityValidator)(createCity) {
                  handleWithGeneric(cityRepository.select(createCity)) { city =>
                    complete(city)
                  }
                }
              }
            },

          )
        }
      )
    }

  }
  override def route = {
    concat(
      healthCheck,
      cityRoutes,
      mainRoute
    )
  }
}



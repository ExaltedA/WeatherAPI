import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.Directives
import akka.http.scaladsl.server.Directives._

trait HealthCheckRoute {
  def healthCheck = {
    Directives.concat(
      path("hello") {
        get {
          complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "<h1>Hello dear dev, your server is up!</h1>"))
        }
      },
      path("ping") {
        get {
          complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "<h1>KABOOM</h1>"))
        }
      }
    )
  }
}

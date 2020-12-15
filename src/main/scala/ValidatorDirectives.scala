import akka.http.scaladsl.server.{Directive0, Directives}
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._

trait ValidatorDirectives extends Directives {
  def validateWith[T](validator: Validator[T])(t: T): Directive0 =
    validator.validate(t) match {
      case Some(apiError) =>
        complete(apiError.statusCode, apiError.message)
      case None =>
        pass
    }
}
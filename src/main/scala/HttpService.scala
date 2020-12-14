import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import io.circe._
import io.circe.parser._

import scala.concurrent.ExecutionContextExecutor
import scala.io.Source
import scala.util.{Failure, Success, Try, Using}

object HttpService {
  def MakeHttpRequest(cityName: String): String = {
    implicit val system: ActorSystem[Nothing] = ActorSystem(Behaviors.empty, "AkkaHttp")
    implicit val executionContext: ExecutionContextExecutor = system.executionContext
    var cityTemperature: Double = 0.0
    var exceptionHttp: String = ""
    val APIKey: String = "bbaf2dcd3c0b40fc475b66811a8223c5"
    val URL: String = s"https://api.openweathermap.org/data/2.5/weather?q=$cityName&appid=$APIKey"
    val requestBody: Try[String] = Using(Source.fromURL(URL)) { source => source.mkString }
    requestBody match {
      case Success(requestValue) =>
        parse(requestValue) match {
          case Right(value) =>
            val cursor: HCursor = value.hcursor
            cursor.downField("main").get[Double]("temp") match {
              case Right(value) => cityTemperature = value - 273
              case Left(value) => exceptionHttp = "oops, something wrong with APi temperature"
            }

        }
      case Failure(_) => exceptionHttp = "city no found"
    }
    val city = CityData(cityName, cityTemperature.toString)
    KafkaProducer.putCityToKafka(city)
    if (exceptionHttp == "")
      cityTemperature.toString
    else exceptionHttp
  }

  def getCities(cities: Vector[CityData]):Vector[CityData] = {
    var tempVector: Vector[CityData] = Vector.empty
    for(city <- cities){
      tempVector :+= CityData(city.name,MakeHttpRequest(city.name))
    }
    tempVector
  }
}
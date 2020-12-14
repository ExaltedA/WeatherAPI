


import akka.actor.TypedActor.dispatcher

import scala.io.Source
import scala.util.{Failure, Success, Using}
import scala.concurrent.{ExecutionContext, Future}
import io.circe._
import io.circe.parser._

import scala.util.Try


class OpenMap(ex:ExecutionContext) {


    val APIkey = "8b14f6830cf4eb4ab2c5a39de27978cf"
    var cache = Vector[CityData]()


  def getVector:Unit = Future[Vector[CityData]]{
      cache
    }

  def receiveCity(city:String):String = {
    val client = s"https://api.openweathermap.org/data/2.5/weather?q=$city&appid=$APIkey"
    val requestBody: Try[String] = Using(Source.fromURL(client)) { source => source.mkString }
    requestBody match {
        case Success(requestValue) =>
          parse(requestValue) match {
            case Right(value) =>
              val cursor: HCursor = value.hcursor

            val res = cursor.downField("main").get[Double]("temp").toString


          }
        case Failure(exception) => {
          s"City not found"
        }
      }

}





}

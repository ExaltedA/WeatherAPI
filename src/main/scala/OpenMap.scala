import akka.actor.typed.{ActorSystem, Behavior}
import akka.actor.typed.scaladsl.Behaviors

import scala.io.Source
import scala.util.{Failure, Success, Try, Using}
import scala.concurrent.{ExecutionContext, ExecutionContextExecutor, Future}
import io.circe._
import io.circe.parser._
import io.circe.syntax._
import driver.system

import scala.io.StdIn.readLine
import scala.util.Try

object OpenMap {
  def apply(): Behavior[String] = Behaviors.setup { context =>

    val APIkey = "8b14f6830cf4eb4ab2c5a39de27978cf"
    var cache = Vector[CityData]


    Behaviors.receiveMessage { message =>
      val city = message // May not work
      println(city)
      val client = s"https://api.openweathermap.org/data/2.5/weather?q=$city&appid=$APIkey"
      val requestBody: Try[String] = Using(Source.fromURL(client)) { source => source.mkString }
      requestBody match {
        case Success(requestValue) =>
          parse(requestValue) match {
            case Right(value) =>
              val cursor: HCursor = value.hcursor
              //            val baz: Decoder.Result[String] = cursor.downField("main")
              println(cursor.downField("main").get[Double]("temp"))
            //system.terminate()
          }
        case Failure(exception) => {
          println("City name is mistyped or unavailable")
        }
      }


      Behaviors.same
    }
  }



}

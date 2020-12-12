

import java.net.URLEncoder

import akka.actor.TypedActor.dispatcher
import akka.http.scaladsl.model.headers
import akka.http.scaladsl.model.HttpMethods._
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpRequest, HttpResponse, Uri}
import akka.http.scaladsl.client.RequestBuilding.Get
import akka.stream.scaladsl.MergeHub.source
import akka.http.scaladsl.model.HttpProtocols._
import akka.http.scaladsl.model.MediaTypes._
import akka.http.scaladsl.model.HttpCharsets._
import akka.http.scaladsl.model.HttpRequest.unapply
import akka.http.scaladsl.model.headers.BasicHttpCredentials
import akka.util.ByteString

import scala.concurrent.Future
import scala.concurrent.duration.DurationInt

object OpenMap extends App{
  val name = "London"
  val APIkey = "8b14f6830cf4eb4ab2c5a39de27978cf"
  val client = s"https://api.openweathermap.org/data/2.5/weather?q=$name&appid=$APIkey"
  // using Eval and Id


  def get(url: String): String = scala.io.Source.fromURL(url).mkString


  println(get(client))
 
  // using Eval and Id
 // val idWeatherInLondon: Either[WeatherError, Current] = idClient.currentByCoords(35.0f, 139.0f)
}

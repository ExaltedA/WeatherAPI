import scala.concurrent.{ExecutionContext, Future}

trait CityRepository {
  def all(): Future[Seq[CityData]]

  def select(createCityData: CreateCityData): Future[CityData]
}


class InMemoryCityRepository()(implicit ex: ExecutionContext) extends CityRepository {

  final case class Response(vector: Vector[CityData])

  private var cities: Vector[CityData] = Vector()

  override def all(): Future[Seq[CityData]] = {
    cities = KafkaConsumer.getCitiesFromKafka
    Future.successful(cities)
  }

  override def select(createCity: CreateCityData): Future[CityData] = Future.successful {
    var newCity = CityData("", "")
    //TODO comment it to disable cashing
    cities = KafkaConsumer.getCitiesFromKafka
    for (city <- cities) {
      if (city.name == createCity.name) {
        newCity = city.copy(name = createCity.name, temperature = city.temperature)
      }
    }
    if (newCity.name == "") {
      val out = HttpService.MakeHttpRequest(createCity.name)
      var res = CityData("", "")
      if (out.contains("city no found") || out.contains("oops")) {
        res = CityData(name = "city not found", temperature = "")
      }
      else {
        res = CityData(name = createCity.name, temperature = out)
      }
      newCity = res.copy(name = res.name, temperature = res.temperature)
    }
    newCity
  }
}
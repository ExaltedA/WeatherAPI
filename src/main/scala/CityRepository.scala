import java.util.UUID

import scala.concurrent.{ExecutionContext, Future}

trait CityRepository {
  def all(): Future[Seq[CityData]]
  def create(createCity:CreateCityData): Future[CityData]
  def put(createAddress:CreateCityData): Future[CityData]
  def delete(createAddress: CreateCityData) : Future[CityData]
  def select(createCityData: CreateCityData): Future[CityData]
}


class InMemoryCityRepository(city:Seq[CityData] = Seq.empty)(implicit ex:ExecutionContext) extends CityRepository {
  private var cities: Vector[CityData] = city.toVector

  override def all(): Future[Seq[CityData]] = Future.successful(cities)


  override def create(createCity: CreateCityData): Future[CityData] = Future.successful {
    val city = CityData(
      id = UUID.randomUUID().toString,
      name = createCity.name,
      temperature = "5"  //createCity.address //TODO: temperature OPENMAP
    )
    cities = cities :+ city
    city
  }

  override def put(createCity:CreateCityData): Future[CityData] = Future.successful {
    var newCity = CityData("","","")
    for(city <- cities){
      if(city.name == createCity.name){
        newCity = city.copy(name = createCity.name, temperature = "")//TODO: OPENMAP
        cities = cities.filter(_ != city)
        cities = cities :+ newCity
      }
    }
    newCity
  }

  override def delete(createData: CreateCityData) = Future.successful{
    var newCity = CityData("","","")
    for(city <- cities){
      if(city.name == createData.name){
        cities = cities.filter(_ != city)
        newCity = city
      }
    }
    newCity
  }

  override def select(createCity: CreateCityData): Future[CityData] = Future.successful{
    var newCity = CityData("","","")
    for(city <- cities){
      if(city.name == createCity.name){
        newCity = city.copy(name = createCity.name, temperature = city.temperature)//TODO: OPENMAP
      }
    }
    newCity
  }
}
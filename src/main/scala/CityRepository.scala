import scala.concurrent.{ExecutionContext, Future}

trait CityRepository {
  def all(): Future[Seq[CityData]]
  def create(createCity:CreateCityData): Future[CityData]
  def put(createAddress:CreateCityData): Future[CityData]
  def delete(createAddress: CreateCityData) : Future[CityData]
  def select(createCityData: CreateCityData): Future[CityData]
}

//, kafkaConsumer: ActorRef
class InMemoryCityRepository(api: OpenMap)(implicit ex:ExecutionContext) extends CityRepository {

  final case class Response(vector: Vector[CityData])
  private var cities: Vector[CityData] = Vector(
    CityData( "Nur-Sultan", "+10"),
    CityData( "Paris", "-5")
  )
  /*private def updateRepo():Future[Vector[CityData]] ={

    Future.successful()
  }*/
  override def all(): Future[Seq[CityData]] ={
    //ask for vector
    Future.successful(cities)
  }


  override def create(createCity: CreateCityData): Future[CityData] = Future.successful {
    val city = CityData(
      name = createCity.name,
      temperature = "5"  //createCity.address //TODO: temperature OPENMAP
    )
    cities = cities :+ city
    city
  }

  override def put(createCity:CreateCityData): Future[CityData] = Future.successful {
    var newCity = CityData("","")
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
    var newCity = CityData("","")
    for(city <- cities){
      if(city.name == createData.name){
        cities = cities.filter(_ != city)
        newCity = city
      }
    }
    newCity
  }

  override def select(createCity: CreateCityData): Future[CityData] = Future.successful{
    var newCity = CityData("","")
    //Update
    for(city <- cities){
      if(city.name == createCity.name){
        newCity = city.copy(name = createCity.name, temperature = city.temperature)//TODO: OPENMAP
      }

    }
    if(newCity.name == ""){
      val res = CityData(name = createCity.name, temperature = api.receiveCity(createCity.name))
       newCity = res.copy(name = res.name, temperature = res.temperature)
    }
    newCity
  }
}
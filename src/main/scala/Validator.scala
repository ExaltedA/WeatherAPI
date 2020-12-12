trait Validator[T] {
  def validate(t: T): Option[ApiError]
}
//TODO: if OPENAPI fails then validate
object CreateCityValidator extends Validator[CreateCityData] {
  def validate(createAddress: CreateCityData): Option[ApiError] =
    if (createAddress.name.isEmpty)
      Some(ApiError.emptyTitleField)
    else
      None
}

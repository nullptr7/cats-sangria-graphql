package com.github.nullptr7.schema

import cats.effect.Effect
import com.github.nullptr7.models.City
import com.github.nullptr7.repo.MasterRepo
import sangria.schema._

object CityType {

  def apply[F[_]: Effect]: ObjectType[MasterRepo[F], City] =
    ObjectType(
      name     = "City",
      fieldsFn = () => fields(

        Field(
          name        = "name",
          fieldType   = StringType,
          description = Some("City name."),
          resolve     = _.value.name),

        Field(
          name        = "country",
          fieldType   = CountryType[F],
          description = Some("Country in which this city resides."),
          resolve     = e => CountryType.Deferred.ByCode(e.value.countryCode)
          ),

        Field(
          name        = "district",
          fieldType   = StringType,
          description = Some("District in which this city resides."),
          resolve     = _.value.district),

        Field(
          name        = "population",
          fieldType   = IntType,
          description = Some("City population."),
          resolve     = _.value.population),

        )
      )

}

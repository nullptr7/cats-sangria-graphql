package com.github.nullptr7.schema

import cats.effect._
import cats.effect.implicits._
import com.github.nullptr7.repo.MasterRepo
import sangria.schema._

object QueryType {

  val NamePattern: Argument[String] =
    Argument(
      name         = "namePattern",
      argumentType = OptionInputType(StringType),
      description  = "SQL-style pattern for city name, like \"San %\".",
      defaultValue = ""
      )

  val Code: Argument[String] =
    Argument(
      name         = "code",
      argumentType = OptionInputType(StringType),
      description  = "Unique code of a country.",
      defaultValue = ""
      )

  def apply[F[_]: Effect]: ObjectType[MasterRepo[F], Unit] =
    ObjectType(
      name  = "Query",
      fields = fields(

        Field(
          name        = "users",
          fieldType   = UserType[F],
          description = Some("Returns users"),
          resolve     = c => c.ctx.city.fetchUser.toIO.unsafeToFuture
          ),

        Field(
          name        = "cities",
          fieldType   = ListType(CityType[F]),
          description = Some("Returns cities with the given name pattern, if any."),
          arguments   = List(NamePattern),
          resolve     = c => c.ctx.city.fetchAll(c.argOpt(NamePattern)).toIO.unsafeToFuture
          ),

        Field(
          name        = "country",
          fieldType   = OptionType(CountryType[F]),
          description = Some("Returns the country with the given code, if any."),
          arguments   = List(Code),
          resolve     = c => c.ctx.country.fetchByCode(c.arg(Code)).toIO.unsafeToFuture
          ),

        Field(
          name        = "countries",
          fieldType   = ListType(CountryType[F]),
          description = Some("Returns all countries."),
          resolve     = c => c.ctx.country.fetchAll.toIO.unsafeToFuture
          ),

        )
      )

  def schema[F[_]: Effect]: Schema[MasterRepo[F], Unit] =
    Schema(QueryType[F])
}

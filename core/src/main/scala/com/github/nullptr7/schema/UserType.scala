package com.github.nullptr7.schema

import cats.effect.Effect
import com.github.nullptr7.models.User
import com.github.nullptr7.repo.MasterRepo
import sangria.schema.{Field, IntType, ObjectType, StringType, fields}

object UserType {

  def apply[F[_] : Effect]: ObjectType[MasterRepo[F], User] =
    ObjectType(
      name = "User",
      fieldsFn = () => fields(

        Field(
          name = "id",
          fieldType = StringType,
          description = Some("Id of the user"),
          resolve = _.value.name),

        Field(
          name = "name",
          fieldType = StringType,
          description = Some("Name of the user"),
          resolve = _.value.name),

        Field(
          name = "email",
          fieldType = StringType,
          description = Some("Email of the user"),
          resolve = _.value.email),

        Field(
          name = "password",
          fieldType = StringType,
          description = Some("Password of the user"),
          resolve = _.value.password),
        Field(
          name = "addressLine",
          fieldType = StringType,
          description = Some("AddressLine 1"),
          resolve = _.value.addressLine),
        Field(
          name = "pin",
          fieldType = IntType,
          description = Some("Pincode of the place"),
          resolve = _.value.pin),
        Field(
          name = "city",
          fieldType = StringType,
          description = Some("City of residence"),
          resolve = _.value.city),
        Field(
          name = "country",
          fieldType = StringType,
          description = Some("Country of residence"),
          resolve = _.value.country),
        Field(
          name = "role",
          fieldType = StringType,
          description = Some("Role of the user"),
          resolve = _.value.role),

        )
      )
}

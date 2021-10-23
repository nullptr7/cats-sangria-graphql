package com.github.nullptr7.schema

import cats.effect.Effect
import com.github.nullptr7.models.Language
import com.github.nullptr7.repo.MasterRepo
import sangria.execution.deferred.Deferred
import sangria.schema._

object LanguageType {

  object Deferred {
    case class ByCountryCode(code: String) extends Deferred[List[Language]]
  }

  def apply[F[_] : Effect]: ObjectType[MasterRepo[F], Language] =
    ObjectType(
      name = "Language",
      fieldsFn = () => fields(

        Field(
          name = "language",
          fieldType = StringType,
          resolve = _.value.language
          ),

        Field(
          name = "isOfficial",
          fieldType = BooleanType,
          resolve = _.value.isOfficial
          ),

        Field(
          name = "percentage",
          fieldType = FloatType,
          resolve = _.value.percentage.toDouble
          ),

        )
      )

}

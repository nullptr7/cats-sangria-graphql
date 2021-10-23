package com.github.nullptr7.models

final case class Country(code: String,
                         name: String,
                         continent: String,
                         region: String,
                         surfacearea: Float,
                         indepyear: Option[Short],
                         population: Int,
                         lifeexpectancy: Option[Float],
                         gnp: Option[BigDecimal],
                         gnpold: Option[BigDecimal],
                         localname: String,
                         governmentform: String,
                         headofstate: Option[String],
                         capitalId: Option[Int],
                         code2: String
                        )

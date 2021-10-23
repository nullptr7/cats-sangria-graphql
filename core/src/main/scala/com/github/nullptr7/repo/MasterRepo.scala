package com.github.nullptr7.repo

import cats.effect._
import io.chrisdavenport.log4cats.Logger

final case class MasterRepo[F[_]](city: CityRepo[F], country: CountryRepo[F], language: LanguageRepo[F])

object MasterRepo {

  def fetch[F[_] : Sync : Logger]: MasterRepo[F] =
    MasterRepo(
      CityRepo.fetch,
      CountryRepo.fetch,
      LanguageRepo.fetch
      )

}

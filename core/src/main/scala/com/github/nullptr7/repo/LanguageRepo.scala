package com.github.nullptr7.repo

import cats.effect.Sync
import cats.implicits._
import com.github.nullptr7.models.Language
import io.chrisdavenport.log4cats.Logger

trait LanguageRepo[F[_]] {
  def fetchByCountryCode(code: String): F[List[Language]]
  def fetchByCountryCodes(codes: List[String]): F[Map[String, List[Language]]]
}

object LanguageRepo {

  def fetch[F[_]: Sync: Logger]: LanguageRepo[F] =
    new LanguageRepo[F] {

      def fetchByCountryCode(code: String): F[List[Language]] =
        Logger[F].info(s"LanguageRepo.fetchByCountryCode($code)") *> List.empty[Language].pure[F]

      def fetchByCountryCodes(codes: List[String]): F[Map[String, List[Language]]] =
        Map.empty[String, List[Language]].pure[F]
    }

}

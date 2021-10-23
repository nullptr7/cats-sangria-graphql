package com.github.nullptr7.repo

import cats.effect._
import cats.implicits._
import com.github.nullptr7.models.Country
import io.chrisdavenport.log4cats.Logger

trait CountryRepo[F[_]] {
  def fetchByCode(code: String): F[Option[Country]]
  def fetchAll: F[List[Country]]
  def fetchByCodes(codes: List[String]): F[List[Country]]
  def update(code: String, newName: String): F[Option[Country]]
}

object CountryRepo {

  def fetch[F[_]: Sync: Logger]: CountryRepo[F] =
    new CountryRepo[F] {

      def fetchByCode(code: String): F[Option[Country]] = {
        Logger[F].info(s"CityRepo.fetchByNamePattern($code)") *> Option.empty[Country].pure[F]
      }

      def fetchByCodes(codes: List[String]): F[List[Country]] =
        List.empty[Country].pure[F]

      def fetchAll: F[List[Country]] =
        List.empty[Country].pure[F]

      def update(code: String, newName: String): F[Option[Country]] =
        Option.empty[Country].pure[F]

    }

}

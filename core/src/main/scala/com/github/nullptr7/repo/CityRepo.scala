package com.github.nullptr7.repo

import cats.effect._
import cats.implicits._
import com.github.nullptr7.ApplicationConfig
import com.github.nullptr7.models.{City, User}
import doobie._
import doobie.implicits._
import doobie.util.ExecutionContexts
import io.chrisdavenport.log4cats.Logger

trait CityRepo[F[_]] {
  def fetchAll(pat: Option[String]): F[List[City]]

  def fetchByCountryCode(code: String): F[List[City]]

  def fetchUser: F[User]
}

object CityRepo {

  def fetch[F[_] : Sync : Logger]: CityRepo[F] =
    new CityRepo[F] {
      def fetchAll(pat: Option[String]): F[List[City]] =
        Logger[F].info(s"CityRepo.fetchByNamePattern($pat)") *>
        List.empty[City].pure[F]

      def fetchByCountryCode(code: String): F[List[City]] =
        Logger[F].info(s"CityRepo.fetchByCountryCode($code)") *> List.empty[City].pure[F]

      def fetchUser: F[User] = Repo.fetchUser1[F]
    }

  object Repo extends ApplicationConfig {
    def fetchUser1[F[_] : Sync : Logger]: F[User] = {

      implicit val cs: ContextShift[IO] = IO.contextShift(ExecutionContexts.synchronous)
      import cats.effect.Blocker
      import doobie.util.ExecutionContexts
      val xa = Transactor.fromDriverManager[IO](
        driver,
        url,
        username,
        password,
        Blocker.liftExecutionContext(ExecutionContexts.synchronous)
        )
      Logger[F].info("calling postgres") *>
      Sync[F].delay {
        sql"select * from users_t"
          .query[User]
          .unique
          .transact(xa)
          .unsafeRunSync()
      }
    }
  }


}
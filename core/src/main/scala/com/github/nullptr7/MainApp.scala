package com.github.nullptr7

import cats.effect._
import cats.implicits._
import com.github.nullptr7.sangria.SangriaGraphQL
import com.github.nullptr7.schema.{QueryType, WorldDeferredResolver}
import io.chrisdavenport.log4cats.Logger
import io.chrisdavenport.log4cats.SelfAwareStructuredLogger
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger
import org.http4s.HttpRoutes
import org.http4s.server.blaze.BlazeServerBuilder

import scala.concurrent.ExecutionContext.Implicits.global
import org.http4s._
import org.http4s.dsl._
import org.http4s.headers.Location
import org.http4s.implicits._
import org.http4s.server.Server
import _root_.sangria.schema.Schema
import com.github.nullptr7.repo.MasterRepo

import scala.concurrent.ExecutionContext

object MainApp extends IOApp {

  def server[F[_] : ConcurrentEffect : ContextShift : Timer](routes: HttpRoutes[F]): Resource[F, Server[F]] =
    BlazeServerBuilder[F](global).bindHttp(8080, "localhost")
                                 .withHttpApp(routes.orNotFound)
                                 .resource

  // Construct a GraphQL implementation based on our Sangria definitions.
  def graphQL[F[_] : Effect : ContextShift : Logger](blockingContext: ExecutionContext): GraphQL[F] = {
    val v: F[MasterRepo[F]] = MasterRepo.fetch.pure[F]
    SangriaGraphQL[F](Schema(query = QueryType[F]), WorldDeferredResolver[F], v, blockingContext)
  }

  def resource[F[_] : ConcurrentEffect : ContextShift : Timer](implicit L: Logger[F]): Resource[F, Server[F]] =
    for {
      b <- Blocker[F]
      gql = graphQL[F](b.blockingContext)
      rts = GraphQLRoutes[F](gql) <+> playgroundOrElse(b)
      svr <- server[F](rts)
    } yield svr

  def playgroundOrElse[F[_] : Sync : ContextShift](blocker: Blocker): HttpRoutes[F] = {
    object dsl extends Http4sDsl[F]
    import dsl._

    HttpRoutes.of[F] {
      case GET -> Root / "playground.html" =>
        StaticFile.fromResource[F]("/playground.html", blocker)
                  .getOrElseF(NotFound())
      case _                               => PermanentRedirect(Location(uri"""/playground.html"""))
    }
  }

  override def run(args: List[String]): IO[ExitCode] = {
    implicit val logger: SelfAwareStructuredLogger[IO] = Slf4jLogger.getLogger[IO]
    resource[IO].use(_ => IO.never.as(ExitCode.Success))
  }
}

package com.github.nullptr7.schema

import cats.effect._
import cats.effect.implicits._
import cats.implicits._
import com.github.nullptr7.repo.MasterRepo
import sangria.execution.deferred.{Deferred, DeferredResolver}

import scala.concurrent.ExecutionContext
import scala.concurrent._
import scala.reflect.ClassTag
import scala.util.Success

object WorldDeferredResolver {

  def apply[F[_] : Effect]: DeferredResolver[MasterRepo[F]] =
    new DeferredResolver[MasterRepo[F]] {

      def resolve(deferred: Vector[Deferred[Any]], ctx: MasterRepo[F], queryState: Any)
                 (implicit ec: ExecutionContext): Vector[Future[Any]] = {

        // So what we're going to do is create a map of Deferred to Promise and the complete them
        // asynchronously through various batch queries. We need to figure out how to fail them when
        // something bad happens but we'll be optimistic for now.

        // Deduplicate our Deferred and associate each with an unfulfilled Promise
        // Note that computing this is unsafe because reference equality matters for Promises.
        val promises: Map[Deferred[Any], Promise[Any]] =
          deferred.map(d => d -> Promise[Any]()).toMap

        // Select the distinct Deferred of the given class. This is freaking desperate but we're
        // given Any so not a whole lot of choices.
        def select[A <: Deferred[Any] : ClassTag]: List[A] =
          promises.keys.collect { case a: A => a }.toList

        // Complete the promise associated with a Deferred
        def complete[A](d: Deferred[A], a: A): F[Unit] =
          Effect[F].delay(promises(d).complete(Success(a))).void

        // Complete a bunch of countries by doing a batch database query
        def completeCountries(codes: List[CountryType.Deferred.ByCode]): F[Unit] =
          for {
            cs <- ctx.country.fetchByCodes(codes.map(_.code))
            _ <- cs.traverse(c => complete(CountryType.Deferred.ByCode(c.code), c))
          } yield ()

        // Complete a bunch of languages by doing a batch database query
        def completeLanguages(codes: List[LanguageType.Deferred.ByCountryCode]): F[Unit] =
          for {
            m <- ctx.language.fetchByCountryCodes(codes.map(_.code))
            _ <- m.toList.traverse { case (c, ls) => complete(LanguageType.Deferred.ByCountryCode(c), ls) }
          } yield ()

        // WARNING WARNING WARNING
        completeCountries(select[CountryType.Deferred.ByCode]).toIO.unsafeToFuture
        completeLanguages(select[LanguageType.Deferred.ByCountryCode]).toIO.unsafeToFuture
        // Let's hope there are no more cases, who the hell knows. Any orphaned Promises will just
        // wait forever and eventually we'll time out. I assume.

        // Anyway we're done here.
        deferred.map(promises(_).future)

      }

    }

}

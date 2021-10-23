package com.github.nullptr7.sangria

import sangria.ast._
import sangria.schema._
import sangria.execution._
import sangria.validation._
import sangria.marshalling.circe._
import sangria.execution.deferred._
import sangria.execution.WithViolations
import sangria.parser.{QueryParser, SyntaxError}

import cats.effect._
import cats.implicits._

import io.circe.Json._
import io.circe.{Json, JsonObject}
import io.circe.optics.JsonPath.root

import scala.util.{Failure, Success}
import scala.concurrent.ExecutionContext

import com.github.nullptr7.GraphQL

object SangriaGraphQL {

  // Some circe lenses
  private val queryStringLens   = root.query.string
  private val operationNameLens = root.operationName.string
  private val variablesLens     = root.variables.obj

  // Format a SyntaxError as a GraphQL `errors`
  private def formatSyntaxError(e: SyntaxError): Json = Json.obj(
    "errors" -> arr(
      obj("message" -> fromString(e.getMessage),
          "locations" -> arr(obj(
            "line" -> fromInt(e.originalError.position.line),
            "column" -> fromInt(e.originalError.position.column)
            ))
          )
      ))

  // Format a WithViolations as a GraphQL `errors`
  private def formatWithViolations(e: WithViolations): Json = obj(
    "errors" -> fromValues(e.violations.map {
      case v: AstNodeViolation =>
        obj(
          "message" -> fromString(v.errorMessage),
          "locations" -> fromValues(v.locations.map(loc => obj("line" -> fromInt(loc.line),
                                                               "column" -> fromInt(loc.column)))
                                    )
          )
      case v                   => obj("message" -> fromString(v.errorMessage))
    }))

  // Format a String as a GraphQL `errors`
  private def formatString(s: String): Json = obj(
    "errors" -> arr(obj("message" -> fromString(s)))
    )

  // Format a Throwable as a GraphQL `errors`
  private def formatThrowable(e: Throwable): Json = obj(
    "errors" -> arr(obj("class" -> fromString(e.getClass.getName), "message" -> fromString(e.getMessage)))
    )

  // Partially-applied constructor
  def apply[F[_]] = new Partial[F]

  final class Partial[F[_]] {

    // The rest of the constructor
    def apply[A](schema: Schema[A, Unit], deferredResolver: DeferredResolver[A], userContext: F[A],
                 blockingExecutionContext: ExecutionContext)
                (implicit F: Async[F]): GraphQL[F] =
      new GraphQL[F] {
        // De-structure `request` and delegate to the other overload.
        def query(request: Json): F[Either[Json, Json]] = {
          val queryString   = queryStringLens.getOption(request)
          val operationName = operationNameLens.getOption(request)
          val variables     = variablesLens.getOption(request).getOrElse(JsonObject())
          queryString match {
            case Some(qs) => query(qs, operationName, variables)
            case None     => fail(formatString("No 'query' property was present in the request."))
          }
        }

        // Parse `query` and execute.
        def query(query: String, operationName: Option[String], variables: JsonObject): F[Either[Json, Json]] =
          QueryParser.parse(query) match {
            case Success(ast)                    => exec(schema, userContext, ast, operationName, variables)(
              blockingExecutionContext)
            case Failure(e@SyntaxError(_, _, _)) => fail(formatSyntaxError(e))
            case Failure(e)                      => fail(formatThrowable(e))
          }

        // Lift a `Json` into the error side of our effect.
        def fail(j: Json): F[Either[Json, Json]] =
          F.pure(j.asLeft)

        // Execute a GraphQL query with Sangria, lifting into IO for safety and sanity.
        def exec(schema: Schema[A, Unit], userContext: F[A], query: Document, operationName: Option[String],
                 variables: JsonObject)
                (implicit ec: ExecutionContext): F[Either[Json, Json]] =
          userContext.flatMap { ctx =>
            F.async { (cb: Either[Throwable, Json] => Unit) =>
              Executor.execute(
                schema = schema,
                deferredResolver = deferredResolver,
                queryAst = query,
                userContext = ctx,
                variables = Json.fromJsonObject(variables),
                operationName = operationName,
                exceptionHandler = ExceptionHandler {
                  case (_, e) ⇒ HandledException(e.getMessage)
                }
                ).onComplete {
                case Success(value) => cb(Right(value))
                case Failure(error) => cb(Left(error))
              }
            }
          }.attempt.flatMap {
            case Right(json)               => F.pure(json.asRight)
            case Left(err: WithViolations) => fail(formatWithViolations(err))
            case Left(err)                 => fail(formatThrowable(err))
          }

      }
  }

}

name := "cats-sangria-graphql"

version := "0.1"

scalaVersion := "2.12.13"

//headerLicense := None

lazy val catsEffectVersion    = "2.1.3"
lazy val catsVersion          = "2.1.1"
lazy val circeVersion         = "0.13.0"
lazy val fs2Version           = "2.3.0"
lazy val kindProjectorVersion = "0.9.10"
lazy val log4catsVersion      = "1.0.1"
lazy val sangriaCirceVersion  = "1.3.0"
lazy val sangriaVersion       = "1.4.2"
lazy val scala12Version       = "2.12.13"
lazy val http4sVersion        = "0.21.4"
lazy val slf4jVersion         = "1.7.30"
lazy val doobieVersion        = "0.13.1"

enablePlugins(CodegenPlugin)

lazy val scalacSettings = Seq(
  scalacOptions ++=
  Seq(
    "-deprecation",                      // Emit warning and location for usages of deprecated APIs.
    "-encoding", "utf-8",                // Specify character encoding used by source files.
    "-explaintypes",                     // Explain type errors in more detail.
    "-feature",                          // Emit warning and location for usages of features that should be imported explicitly.
    "-language:existentials",            // Existential types (besides wildcard types) can be written and inferred
    "-language:higherKinds",             // Allow higher-kinded types
    "-language:implicitConversions",     // Allow definition of implicit functions called views
    "-unchecked",                        // Enable additional warnings where generated code depends on assumptions.
    "-Xcheckinit",                       // Wrap field accessors to throw an exception on uninitialized access.
    "-Xfatal-warnings",                  // Fail the compilation if there are any warnings.
    "-Xfuture",                          // Turn on future language features.
    "-Xlint:adapted-args",               // Warn if an argument list is modified to match the receiver.
    "-Xlint:by-name-right-associative",  // By-name parameter of right associative operator.
    "-Xlint:constant",                   // Evaluation of a constant arithmetic expression results in an error.
    "-Xlint:delayedinit-select",         // Selecting member of DelayedInit.
    "-Xlint:doc-detached",               // A Scaladoc comment appears to be detached from its element.
    "-Xlint:inaccessible",               // Warn about inaccessible types in method signatures.
    "-Xlint:infer-any",                  // Warn when a type argument is inferred to be `Any`.
    "-Xlint:missing-interpolator",       // A string literal appears to be missing an interpolator id.
    "-Xlint:nullary-override",           // Warn when non-nullary `def f()' overrides nullary `def f'.
    "-Xlint:nullary-unit",               // Warn when nullary methods return Unit.
    "-Xlint:option-implicit",            // Option.apply used implicit view.
    "-Xlint:package-object-classes",     // Class or object defined in package object.
    "-Xlint:poly-implicit-overload",     // Parameterized overloaded implicit methods are not visible as view bounds.
    "-Xlint:private-shadow",             // A private field (or class parameter) shadows a superclass field.
    "-Xlint:stars-align",                // Pattern sequence wildcard must align with sequence component.
    "-Xlint:type-parameter-shadow",      // A local type parameter shadows a type already in scope.
    "-Xlint:unsound-match",              // Pattern match may not be typesafe.
    "-Yno-adapted-args",                 // Do not adapt an argument list (either by inserting () or creating a tuple) to match the receiver.
    // "-Yno-imports",                      // No predef or default imports
    "-Ypartial-unification",             // Enable partial unification in type constructor inference
    "-Ywarn-dead-code",                  // Warn when dead code is identified.
    "-Ywarn-extra-implicit",             // Warn when more than one implicit parameter section is defined.
    "-Ywarn-inaccessible",               // Warn about inaccessible types in method signatures.
    "-Ywarn-infer-any",                  // Warn when a type argument is inferred to be `Any`.
    "-Ywarn-nullary-override",           // Warn when non-nullary `def f()' overrides nullary `def f'.
    "-Ywarn-nullary-unit",               // Warn when nullary methods return Unit.
    "-Ywarn-numeric-widen",              // Warn when numerics are widened.
    "-Ywarn-unused:implicits",           // Warn if an implicit parameter is unused.
    "-Ywarn-unused:imports",             // Warn if an import selector is not referenced.
    "-Ywarn-unused:locals",              // Warn if a local definition is unused.
    "-Ywarn-unused:params",              // Warn if a value parameter is unused.
    // "-Ywarn-unused:patvars",             // Warn if a variable bound in a pattern is unused.
    "-Ywarn-unused:privates",            // Warn if a private member is unused.
    "-Ywarn-value-discard",              // Warn when non-Unit expression results are unused.
    "-Ywarn-macros:before", // via som
    "-Yrangepos" // for longer squiggles
    )
  ,
  scalacOptions in (Compile, console) --= Seq("-Xfatal-warnings", "-Ywarn-unused:imports", "-Yno-imports"),
  )

lazy val commonSettings = scalacSettings ++ Seq(
  organization := "com.github.nullptr7",
//  licenses ++= Seq(("MIT", url("http://opensource.org/licenses/MIT"))),
  addCompilerPlugin("org.spire-math" %% "kind-projector" % kindProjectorVersion))

lazy val core = project
  .settings(commonSettings)
  .settings(
    name := "cats-sangria-graphql",
    description := "Sangria with Cats",
    libraryDependencies ++= Seq(
      "org.typelevel"        %% "cats-core"           % catsVersion,
      "org.typelevel"        %% "cats-effect"         % catsEffectVersion,
      "co.fs2"               %% "fs2-core"            % fs2Version,
      "co.fs2"               %% "fs2-io"              % fs2Version,
      "org.sangria-graphql"  %% "sangria"             % sangriaVersion,
      "org.sangria-graphql"  %% "sangria-circe"       % sangriaCirceVersion,
      "org.http4s"           %% "http4s-dsl"          % http4sVersion,
      "org.http4s"           %% "http4s-blaze-server" % http4sVersion,
      "org.http4s"           %% "http4s-circe"        % http4sVersion,
      "io.circe"             %% "circe-optics"        % circeVersion,
      "io.chrisdavenport"    %% "log4cats-slf4j"      % log4catsVersion,
      "org.slf4j"            %  "slf4j-simple"        % slf4jVersion,
      "org.tpolecat"         %% "doobie-core"         % doobieVersion,
      "org.tpolecat"         %% "doobie-postgres"     % doobieVersion,
      "org.postgresql"        % "postgresql"          % "42.2.20",
      "com.typesafe"          % "config"              % "1.4.1",
      "com.github.ghostdogpr" %% "caliban-client" % "0.10.1"

      )
    )

lazy val sangria_cats = project.in(file("."))
                               .settings(commonSettings)
                               .dependsOn(core)
                               .aggregate(core)
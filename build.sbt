lazy val lightql = project
  .in(file("."))
  .settings(
    skip in publish := true
  )
  .aggregate(dsl, `elastic-http`, `elastic6-http`)

lazy val dsl = project
  .in(file("lightql-dsl"))
  .enablePlugins(AutomateHeaderPlugin)
  .settings(
    name := "lightql-dsl",
    version := Version.Dsl,
    libraryDependencies ++= Seq(
      Library.fastParse,
      Library.nscalaTime,
      Library.scalaTest % Test
    ),
    initialCommands := "import com.tecsisa.lightql.parser._"
  )

lazy val httpDefaultSettings = Seq(
  name := "lightql-elastic-http",
  libraryDependencies ++= Seq(
    Library.testContainers          % "it",
    Library.testContainersElastic   % "it",
    Library.log4jApi                % "it",
    Library.log4jCore               % "it",
    Library.log4jSlfj4Impl          % "it"
  ),
  fork in IntegrationTest := true
) ++ Defaults.itSettings

lazy val `elastic-http` = project
  .in(file("lightql-elastic-http"))
  .enablePlugins(AutomateHeaderPlugin)
  .configs(IntegrationTest)
  .settings(httpDefaultSettings: _*)
  .settings(
    version := Version.ElasticMaterializer,
    libraryDependencies ++= Seq(
      Library.elastic4sClient,
      Library.elastic4sTestkit % "it"
    ),
    envVars in IntegrationTest := Map("ELASTIC_DOCKER_TAG" -> "7.11.0")
   )
  .dependsOn(dsl)

lazy val `elastic6-http` = project
  .in(file("lightql-elastic6-http"))
  .enablePlugins(AutomateHeaderPlugin)
  .configs(IntegrationTest)
  .settings(httpDefaultSettings: _*)
  .settings(
    version := Version.Elastic6Materializer,
    libraryDependencies ++= Seq(
      Library.elastic4s6Client,
      Library.elastic4s6Testkit % "it",
    ),
    envVars in IntegrationTest := Map("ELASTIC_DOCKER_TAG" -> "6.7.2")
  )
  .dependsOn(dsl)

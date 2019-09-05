lazy val lightql = project
  .in(file("."))
  .enablePlugins(NoPublish)
  .disablePlugins(BintrayPlugin)
  .aggregate(dsl, elastic, `elastic-http`, `elastic-test`, docs)

lazy val docs = project
  .enablePlugins(NoPublish, PublishDocs)
  .disablePlugins(BintrayPlugin)
  .dependsOn(`elastic-http`)

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

lazy val elastic = project
  .in(file("lightql-elastic"))
  .enablePlugins(AutomateHeaderPlugin)
  .settings(
    name := "lightql-elastic",
    version := Version.ElasticMaterializer,
    libraryDependencies ++= Seq(
      Library.elastic4s
    )
  )
  .dependsOn(dsl)

lazy val `elastic-http` = project
  .in(file("lightql-elastic-http"))
  .enablePlugins(AutomateHeaderPlugin)
  .settings(
    name := "lightql-elastic-http",
    version := Version.ElasticMaterializer,
    libraryDependencies ++= Seq(
      Library.elastic4sClient
    )
  )
  .dependsOn(elastic)

lazy val `elastic-test` = project
  .in(file("lightql-elastic-test"))
  .enablePlugins(AutomateHeaderPlugin, NoPublish)
  .settings(
    name := "lightql-elastic-test",
    version := Version.ElasticMaterializer,
    libraryDependencies ++= Seq(
      Library.elastic4sTestkit % Test,
      Library.log4jApi         % Test,
      Library.log4jCore        % Test,
      Library.log4jSlfj4Impl   % Test
    ),
    fork in Test := true
  )
  .dependsOn(`elastic-http`)

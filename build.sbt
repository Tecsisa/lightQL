lazy val lightql = project
  .in(file("."))
  .enablePlugins(NoPublish)
  .disablePlugins(BintrayPlugin)
  .aggregate(dsl, elastic, docs)

lazy val docs = project
  .enablePlugins(MicrositesPlugin, NoPublish, PublishUnidoc)
  .disablePlugins(BintrayPlugin)
  .dependsOn(elastic)

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
      Library.elastic4s,
      Library.elastic4sTestkit % Test
    )
  )
  .dependsOn(dsl)

lazy val lightql = project
  .in(file("."))
  .enablePlugins(NoPublish, GitVersioning, AutomateHeaderPlugin)
  .aggregate(elastic)

lazy val elastic = project
  .in(file("lightql-elastic"))
  .enablePlugins(GitVersioning, AutomateHeaderPlugin)
  .settings(
    name := "lightql-elastic",
    version := Version.ElasticMaterializer,
    libraryDependencies ++= Seq(
      "com.tecsisa" %% "lightql-dsl" % Version.Dsl,
      Library.elastic4s,
      Library.elastic4sTestkit % Test
    )
  )

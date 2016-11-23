lazy val `wr-kql` = project
  .in(file("."))
  .enablePlugins(NoPublish, GitVersioning)
  .aggregate(elastic)

lazy val elastic = project
  .in(file("wr-kql-elastic"))
  .enablePlugins(GitVersioning)
  .settings(
    name := "wr-kql-elastic",
    version := Version.ElasticMaterializer,
    libraryDependencies ++= Seq(
      "com.tecsisa.wr" %% "wr-kql-dsl" % Version.Dsl,
      Library.elastic4s,
      Library.elastic4sTestkit % Test
    )
  )

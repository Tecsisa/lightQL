lazy val `wr-kql` = project
  .in(file("."))
  .enablePlugins(NoPublish, GitVersioning)
  .aggregate(dsl, elastic)

lazy val dsl = project
  .in(file("wr-kql-dsl"))
  .enablePlugins(GitVersioning)
  .settings(
    name := "wr-kql-dsl",
    version := Version.Dsl,
    libraryDependencies ++= Seq(
      Library.fastParse,
      Library.nscalaTime,
      Library.scalaTest % Test
    ),
    initialCommands := """|import com.tecsisa.wr.kql.parser.KqlParser._
                          |import com.tecsisa.wr.kql.ast.ClauseTree._
                          |""".stripMargin
  )

lazy val elastic = project
  .in(file("wr-kql-elastic"))
  .enablePlugins(GitVersioning)
  .settings(
    name := "wr-kql-elastic",
    version := Version.ElasticMaterializer,
    libraryDependencies ++= Seq(
      Library.elastic4s,
      Library.elastic4sTestkit % Test
    )
  )
  .dependsOn(dsl)

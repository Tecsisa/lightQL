lazy val lightql = project
  .in(file("."))
  .enablePlugins(NoPublish, GitVersioning, AutomateHeaderPlugin)
  .aggregate(dsl, elastic)

lazy val `docs` = project
  .enablePlugins(MicrositesPlugin)
  .settings(micrositeSettings)

lazy val micrositeSettings = Seq(
  micrositeName := "lightql",
  micrositeDescription := "Easy query language for Elasticsearch",
  micrositeBaseUrl := "lightql",
  micrositeDocumentationUrl := "/lightql/docs/",
  micrositeAuthor := "Tecsisa",
  micrositeHomepage := "http://www.tecsisa.com",
  micrositeExtraMdFiles := Map(file("CONTRIBUTING.md") -> "contributing.md", file("README.md") -> "index.md"),
  micrositeGithubOwner := "Tecsisa",
  micrositeGithubRepo := "lightql"
)

lazy val dsl = project
  .in(file("lightql-dsl"))
  .enablePlugins(GitVersioning, AutomateHeaderPlugin)
  .settings(
    name := "lightql-dsl",
    version := Version.Dsl,
    libraryDependencies ++= Seq(
      Library.fastParse,
      Library.nscalaTime,
      Library.scalaTest % Test
    ),
    initialCommands := """|import com.tecsisa.lightql.parser.LightqlParser._
                          |import com.tecsisa.lightql.ast.ClauseTree._
                          |""".stripMargin
  )

lazy val elastic = project
  .in(file("lightql-elastic"))
  .enablePlugins(GitVersioning, AutomateHeaderPlugin)
  .settings(
    name := "lightql-elastic",
    version := Version.ElasticMaterializer,
    libraryDependencies ++= Seq(
      Library.elastic4s,
      Library.elastic4sTestkit % Test
    )
  )
  .dependsOn(dsl)

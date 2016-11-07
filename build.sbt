lazy val `wr-kql` = project.copy(id = "wr-kql").in(file(".")).enablePlugins(GitVersioning)

name := "wr-kql"

libraryDependencies ++= Vector(
  Library.fastParse,
  Library.elastic4s,
  Library.elastic4sTestkit % "test",
  Library.nscalaTime
)

initialCommands := """|import com.tecsisa.wr.kql.parser.KqlParser._
                      |import com.tecsisa.wr.kql.ast.ClauseTree._
                      |""".stripMargin

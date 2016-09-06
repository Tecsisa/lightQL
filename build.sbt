lazy val `wr-kql` = project.copy(id = "wr-kql").in(file("."))

name := "wr-kql"

libraryDependencies ++= Vector(
  Library.fastParse,
  Library.elastic4s,
  Library.elastic4sTestkit % "test"
)

initialCommands := """|import com.tecsisa.wr.kql.parser.KqlParser._
                      |import com.tecsisa.wr.kql.ast.ClauseTree._
                      |""".stripMargin

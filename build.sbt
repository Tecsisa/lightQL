lazy val `wr-kql` = project
  .copy(id = "wr-kql")
  .in(file("."))

name := "wr-kql"

libraryDependencies ++= Vector(
  Library.fastParse,
  Library.elastic,
  Library.scalaTest % "test",
  Library.scalaCheck % "test"
)

initialCommands := """|import com.tecsisa.wr.kql.parser.KqlParser._
                      |""".stripMargin

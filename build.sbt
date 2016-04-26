lazy val `wr-kql` = project
  .copy(id = "wr-kql")
  .in(file("."))

name := "wr-kql"

libraryDependencies ++= Vector(
  Library.scalaCheck % "test"
)

initialCommands := """|import com.tecsisa.wr.kql._
                      |""".stripMargin

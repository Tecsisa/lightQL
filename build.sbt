lazy val wrEql = project
  .copy(id = "wr-eql")
  .in(file("."))
  .enablePlugins(AutomateHeaderPlugin, GitVersioning)

name := "wr-eql"

libraryDependencies ++= Vector(
  Library.scalaCheck % "test"
)

initialCommands := """|import com.tecsisa.wr.eql._
                      |""".stripMargin

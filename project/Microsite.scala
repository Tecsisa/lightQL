import sbt._
import Keys._

object Microsite extends AutoPlugin {
  import com.typesafe.sbt.SbtGit.git
  import microsites.MicrositesPlugin
  import microsites.MicrositesPlugin.autoImport._
  import microsites._
  import tut.TutPlugin.autoImport._

  override def requires = MicrositesPlugin

  override def trigger = allRequirements

  val micrositeDocumentationBaseUrl = settingKey[String]("Base url for documentation")

  override def projectSettings = Seq(
    scalacOptions in Tut ~= (_.filterNot(Set(
      "-Xfatal-warnings",
      "-Xlint",
      "-Ywarn-unused-import"
    ))),
    micrositeName := "lightQL",
    micrositeDescription := "A minimal external search DSL that compiles to Scala",
    micrositeBaseUrl := "/lightQL",
    micrositeDocumentationBaseUrl := "api",
    micrositeDocumentationUrl := micrositeDocumentationBaseUrl.value + "/#com.tecsisa.lightql.parser.LightqlParser$",
    micrositeAuthor := "Tecsisa",
    micrositeHomepage := "http://www.tecsisa.com",
    micrositeExtraMdFiles := Map(
      file("CONTRIBUTING.md") -> ExtraMdFileConfig("contributing.md",
                                                   "docs",
                                                   Map("title"    -> "\"Contributing\"",
                                                       "position" -> "10"))
    ),
    micrositeGithubOwner := "Tecsisa",
    micrositeGithubRepo := "lightQL",
    git.remoteRepo := scmInfo.value.head.connection
  )
}

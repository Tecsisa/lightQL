import sbt._
import Keys._
import com.typesafe.sbt.SbtGit.git
import microsites.MicrositesPlugin
import microsites.MicrositesPlugin.autoImport._

object Microsite extends AutoPlugin {

  override def requires = MicrositesPlugin

  override def trigger = allRequirements

  override def projectSettings = Seq(
    micrositeName := "lightQL",
    micrositeDescription := "A minimal external DSL for searching that compiles to Scala",
    micrositeBaseUrl := "lightql",
    micrositeDocumentationUrl := "api",
    micrositeAuthor := "Tecsisa",
    micrositeHomepage := "http://www.tecsisa.com",
    micrositeExtraMdFiles := Map(file("CONTRIBUTING.md") -> "contributing.md"),
    micrositeGithubOwner := "Tecsisa",
    micrositeGithubRepo := "lightql",
    git.remoteRepo := scmInfo.value.head.connection
  )
}

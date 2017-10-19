import sbt.Keys._
import sbt._
import sbtunidoc.ScalaUnidocPlugin

/**
  * For projects that are not to be published.
  */
object NoPublish extends AutoPlugin {
  import com.typesafe.sbt.pgp.PgpKeys._

  override def requires = plugins.JvmPlugin

  override def projectSettings = Seq(
    publishArtifact := false,
    publish := {},
    publishLocal := {},
    publishLocalSigned := {},
    publishSigned := {}
  )
}

object Publish extends AutoPlugin {
  import bintray.BintrayPlugin
  import bintray.BintrayPlugin.autoImport._

  override def trigger  = allRequirements
  override def requires = BintrayPlugin

  override def projectSettings = Seq(
    bintrayPackage := "lightQL",
    bintrayOrganization := Some("tecsisa"),
    bintrayRepository := "maven-bintray-repo"
  )
}

object PublishDocs extends AutoPlugin {
  import Microsite.micrositeDocumentationBaseUrl
  import com.typesafe.sbt.site.util.SiteHelpers._
  import microsites.MicrositesPlugin
  import microsites.MicrositesPlugin.autoImport._
  import sbtunidoc.BaseUnidocPlugin.autoImport._
  import sbtunidoc.ScalaUnidocPlugin.autoImport._

  override def requires = plugins.JvmPlugin && MicrositesPlugin && ScalaUnidocPlugin

  def publishOnly(artifactType: String)(config: PublishConfiguration): PublishConfiguration = {
    val newArts = config.artifacts.filter { case (art, _) => art.`type` == artifactType }
    config.withArtifacts(newArts)
  }

  override def projectSettings = Seq(
    doc in Compile := (doc in ScalaUnidoc).value,
    target in unidoc in ScalaUnidoc := crossTarget.value / "api",
    publishConfiguration ~= publishOnly(Artifact.DocType),
    publishLocalConfiguration ~= publishOnly(Artifact.DocType),
    micrositeGitterChannel := false,
    addMappingsToSiteDir(mappings in (ScalaUnidoc, packageDoc), micrositeDocumentationBaseUrl)
  )
}

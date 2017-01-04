import sbt._
import Keys._

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

object PublishUnidoc extends AutoPlugin {
  import sbtunidoc.Plugin._
  import sbtunidoc.Plugin.UnidocKeys._
  import com.typesafe.sbt.site.util.SiteHelpers._
  import Microsite.micrositeDocumentationBaseUrl
  import microsites.MicrositesPlugin
  import microsites.MicrositesPlugin.autoImport._

  override def requires = plugins.JvmPlugin && MicrositesPlugin

  def publishOnly(artifactType: String)(config: PublishConfiguration) = {
    val newArts = config.artifacts.filterKeys(_.`type` == artifactType)
    new PublishConfiguration(config.ivyFile,
                             config.resolverName,
                             newArts,
                             config.checksums,
                             config.logging)
  }

  override def projectSettings = unidocSettings ++ Seq(
    doc in Compile := (doc in ScalaUnidoc).value,
    target in unidoc in ScalaUnidoc := crossTarget.value / "api",
    publishConfiguration ~= publishOnly(Artifact.DocType),
    publishLocalConfiguration ~= publishOnly(Artifact.DocType),
    addMappingsToSiteDir(mappings in (ScalaUnidoc, packageDoc), micrositeDocumentationBaseUrl)
  )
}

import sbt.Keys._
import sbt._
import sbtunidoc.ScalaUnidocPlugin

object PublishDocs extends AutoPlugin {
  import sbtunidoc.BaseUnidocPlugin.autoImport._
  import sbtunidoc.ScalaUnidocPlugin.autoImport._

  override def requires = plugins.JvmPlugin && ScalaUnidocPlugin

  def publishOnly(artifactType: String)(config: PublishConfiguration): PublishConfiguration = {
    val newArts = config.artifacts.filter { case (art, _) => art.`type` == artifactType }
    config.withArtifacts(newArts)
  }

  override def projectSettings = Seq(
    doc in Compile := (doc in ScalaUnidoc).value,
    target in unidoc in ScalaUnidoc := crossTarget.value / "api",
    publishConfiguration ~= publishOnly(Artifact.DocType),
    publishLocalConfiguration ~= publishOnly(Artifact.DocType)
  )
}

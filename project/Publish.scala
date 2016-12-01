import sbt._, Keys._
import com.typesafe.sbt.GitPlugin

/**
  * For projects that are not to be published.
  */
object NoPublish extends AutoPlugin {
  import com.typesafe.sbt.pgp.PgpKeys._

  override def requires = plugins.JvmPlugin && GitPlugin

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

  override def trigger = allRequirements
  override def requires = BintrayPlugin

  override def projectSettings = Seq(
    bintrayPackage := "lightQL",
    bintrayOrganization := Some("tecsisa"),
    bintrayRepository := "maven-bintray-repo"
  )
}


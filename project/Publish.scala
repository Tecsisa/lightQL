import sbt._, Keys._
import com.typesafe.sbt.GitPlugin

/**
  * For projects that are not to be published.
  */
object NoPublish extends AutoPlugin {

  override def requires = plugins.JvmPlugin && GitPlugin

  override def projectSettings = Seq(
    publishArtifact := false,
    publish := {},
    publishLocal := {}
  )
}


import sbt._

object Version {
  final val ScalaVersions       = Seq("2.11.8")
  final val ScalaTest           = "3.0.1"
  final val Elastic4s           = "2.3.1"
  final val Dsl                 = "0.1.3"
  final val ElasticMaterializer = "2.3.4-SNAPSHOT"
}

object Library {
  val elastic4s        = "com.sksamuel.elastic4s" %% "elastic4s-core"    % Version.Elastic4s
  val elastic4sTestkit = "com.sksamuel.elastic4s" %% "elastic4s-testkit" % Version.Elastic4s
  val scalaTest        = "org.scalatest"          %% "scalatest"         % Version.ScalaTest
}
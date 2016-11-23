import sbt._

object Version {
  final val Scala               = "2.11.8"
  final val ScalaTest           = "3.0.1"
  final val Elastic4s           = "2.3.1"
  final val Dsl                 = "0.1.0-SNAPSHOT"
  final val ElasticMaterializer = s"$Elastic4s-SNAPSHOT"
}

object Library {
  val elastic4s        = "com.sksamuel.elastic4s" %% "elastic4s-core"    % Version.Elastic4s
  val elastic4sTestkit = "com.sksamuel.elastic4s" %% "elastic4s-testkit" % Version.Elastic4s
  val scalaTest        = "org.scalatest"          %% "scalatest"         % Version.ScalaTest
}
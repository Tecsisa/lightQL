import sbt._

object Version {
  final val Scala               = "2.11.8"
  final val ScalaTest           = "3.0.1"
  final val FastParse           = "0.4.1"
  final val Elastic4s           = "5.0.0"
  final val NscalaTime          = "2.14.0"
  final val Dsl                 = "0.1.3-SNAPSHOT"
  final val ElasticMaterializer = "5.0.2-SNAPSHOT"
}

object Library {
  val fastParse        = "com.lihaoyi"            %% "fastparse"         % Version.FastParse
  val elastic4s        = "com.sksamuel.elastic4s" %% "elastic4s-core"    % Version.Elastic4s
  val elastic4sTestkit = "com.sksamuel.elastic4s" %% "elastic4s-testkit" % Version.Elastic4s
  val nscalaTime       = "com.github.nscala-time" %% "nscala-time"       % Version.NscalaTime
  val scalaTest        = "org.scalatest"          %% "scalatest"         % Version.ScalaTest
}

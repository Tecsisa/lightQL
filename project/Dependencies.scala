import sbt._

object Version {
  final val ScalaVersions       = Seq("2.11.11", "2.12.2")
  final val ScalaTest           = "3.0.1"
  final val FastParse           = "0.4.2"
  final val Elastic4s           = "5.4.2"
  final val NscalaTime          = "2.16.0"
  final val Dsl                 = "0.4.0"
  final val ElasticMaterializer = "5.4.0"
}

object Library {
  val fastParse        = "com.lihaoyi"            %% "fastparse"         % Version.FastParse
  val elastic4s        = "com.sksamuel.elastic4s" %% "elastic4s-core"    % Version.Elastic4s
  val elastic4sTcp     = "com.sksamuel.elastic4s" %% "elastic4s-tcp"     % Version.Elastic4s
  val elastic4sTestkit = "com.sksamuel.elastic4s" %% "elastic4s-testkit" % Version.Elastic4s
  val nscalaTime       = "com.github.nscala-time" %% "nscala-time"       % Version.NscalaTime
  val scalaTest        = "org.scalatest"          %% "scalatest"         % Version.ScalaTest
}

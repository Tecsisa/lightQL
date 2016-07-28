import sbt._

object Version {
  final val Scala     = "2.11.8"
  final val FastParse = "0.3.7"
  final val Elastic4s = "2.3.1"
}

object Library {
  val fastParse        = "com.lihaoyi"            %% "fastparse"         % Version.FastParse
  val elastic4s        = "com.sksamuel.elastic4s" %% "elastic4s-core"    % Version.Elastic4s
  val elastic4sTestkit = "com.sksamuel.elastic4s" %% "elastic4s-testkit" % Version.Elastic4s
}

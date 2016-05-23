import sbt._

object Version {
  final val Scala      = "2.11.8"
  final val FastParse  = "0.3.7"
  final val Elastic    = "2.3.2"
  final val ScalaCheck = "1.13.0"
  final val ScalaTest  = "3.0.0-RC1"
}

object Library {
  val scalaCheck = "org.scalacheck"    %% "scalacheck"   % Version.ScalaCheck
  val scalaTest  = "org.scalatest"     %%  "scalatest"   % Version.ScalaTest
  val fastParse  = "com.lihaoyi"       %% "fastparse"    % Version.FastParse
  val elastic    = "org.elasticsearch" % "elasticsearch" % Version.Elastic
}

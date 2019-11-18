import sbt._

object Version {
  final val ScalaVersions       = Seq("2.12.7", "2.11.12")
  final val ScalaTest           = "3.0.5"
  final val FastParse           = "1.0.0"
  final val Elastic4s           = "6.7.2"
  final val NscalaTime          = "2.20.0"
  final val Log4j               = "2.9.1"
  final val Dsl                 = "0.11.1-SNAPSHOT"
  final val ElasticMaterializer = "6.7.2-SNAPSHOT"
}

object Library {
  val fastParse        = "com.lihaoyi"              %% "fastparse"         % Version.FastParse
  val elastic4s        = "com.sksamuel.elastic4s"   %% "elastic4s-core"    % Version.Elastic4s
  val elastic4sHttp    = "com.sksamuel.elastic4s"   %% "elastic4s-http"    % Version.Elastic4s
  val elastic4sTestkit = "com.sksamuel.elastic4s"   %% "elastic4s-testkit" % Version.Elastic4s
  val nscalaTime       = "com.github.nscala-time"   %% "nscala-time"       % Version.NscalaTime
  val scalaTest        = "org.scalatest"            %% "scalatest"         % Version.ScalaTest
  val log4jApi         = "org.apache.logging.log4j" % "log4j-api"          % Version.Log4j
  val log4jCore        = "org.apache.logging.log4j" % "log4j-core"         % Version.Log4j
  val log4jSlfj4Impl   = "org.apache.logging.log4j" % "log4j-slf4j-impl"   % Version.Log4j
}

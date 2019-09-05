import sbt._

object Version {
  final val ScalaVersions       = Seq("2.12.7")
  final val ScalaTest           = "3.0.5"
  final val FastParse           = "1.0.0"
  final val Elastic4s           = "7.3.1"
  final val NscalaTime          = "2.20.0"
  final val Log4j               = "2.9.1"
  final val Dsl                 = "0.10.1"
  final val ElasticMaterializer = "7.3.0"
}

object Library {
  val fastParse        = "com.lihaoyi"              %% "fastparse"               % Version.FastParse
  val elastic4s        = "com.sksamuel.elastic4s"   %% "elastic4s-core"          % Version.Elastic4s
  val elastic4sClient  = "com.sksamuel.elastic4s"   %% "elastic4s-client-esjava" % Version.Elastic4s
  val elastic4sTestkit = "com.sksamuel.elastic4s"   %% "elastic4s-testkit"       % Version.Elastic4s
  val nscalaTime       = "com.github.nscala-time"   %% "nscala-time"             % Version.NscalaTime
  val scalaTest        = "org.scalatest"            %% "scalatest"               % Version.ScalaTest
  val log4jApi         = "org.apache.logging.log4j" % "log4j-api"                % Version.Log4j
  val log4jCore        = "org.apache.logging.log4j" % "log4j-core"               % Version.Log4j
  val log4jSlfj4Impl   = "org.apache.logging.log4j" % "log4j-slf4j-impl"         % Version.Log4j
}

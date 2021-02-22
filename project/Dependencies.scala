import sbt._

object Version {
  final val ScalaVersions         = Seq("2.13.4", "2.12.13")
  final val ScalaTest             = "3.2.3"
  final val TestContainers        = "0.39.1"
  final val FastParse             = "2.3.1"
  final val Elastic4s             = "7.11.0"
  final val Elastic4s_6           = "6.7.8"
  final val NscalaTime            = "2.26.0"
  final val Log4j                 = "2.14.0"
  final val Dsl                   = "0.12.4-SNAPSHOT"
  final val ElasticMaterializer   = "7.3.4-SNAPSHOT"
  final val Elastic6Materializer  = "6.7.6-SNAPSHOT"
}

object Library {
  val fastParse             = "com.lihaoyi"              %% "fastparse"                          % Version.FastParse
  val elastic4s             = "com.sksamuel.elastic4s"   %% "elastic4s-core"                     % Version.Elastic4s
  val elastic4sClient       = "com.sksamuel.elastic4s"   %% "elastic4s-client-esjava"            % Version.Elastic4s
  val elastic4s6Client      = "com.sksamuel.elastic4s"   %% "elastic4s-http"                     % Version.Elastic4s_6
  val elastic4sTestkit      = "com.sksamuel.elastic4s"   %% "elastic4s-testkit"                  % Version.Elastic4s
  val elastic4s6Testkit     = "com.sksamuel.elastic4s"   %% "elastic4s-testkit"                  % Version.Elastic4s_6
  val nscalaTime            = "com.github.nscala-time"   %% "nscala-time"                        % Version.NscalaTime
  val scalaTest             = "org.scalatest"            %% "scalatest"                          % Version.ScalaTest
  val testContainers        = "com.dimafeng"             %% "testcontainers-scala-scalatest"     % Version.TestContainers
  val testContainersElastic = "com.dimafeng"             %% "testcontainers-scala-elasticsearch" % Version.TestContainers
  val log4jApi              = "org.apache.logging.log4j" %  "log4j-api"                          % Version.Log4j
  val log4jCore             = "org.apache.logging.log4j" %  "log4j-core"                         % Version.Log4j
  val log4jSlfj4Impl        = "org.apache.logging.log4j" %  "log4j-slf4j-impl"                   % Version.Log4j
}

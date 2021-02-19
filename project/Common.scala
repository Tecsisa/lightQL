import sbt._
import sbt.Keys._
import sbt.plugins.JvmPlugin
import de.heikoseeberger.sbtheader._
import de.heikoseeberger.sbtheader.HeaderPlugin.autoImport._
import com.typesafe.sbt.GitPlugin
import org.scalafmt.sbt.ScalafmtPlugin.autoImport.scalafmtOnCompile
import xerial.sbt.Sonatype.SonatypeKeys._

object Common extends AutoPlugin {

  final val headerLic =
    Some(
      HeaderLicense.Custom(
        "Copyright (C) 2016 - 2021 TECNOLOGIA, SISTEMAS Y APLICACIONES S.L. <http://www.tecsisa.com>")
    )

  override def requires = JvmPlugin && GitPlugin && HeaderPlugin

  override def trigger = allRequirements

  override def projectSettings =
    Seq(
      organization := "com.tecsisa",
      organizationName := "Tecnología, Sistemas y Aplicaciones S.L.",
      organizationHomepage := Some(url("http://www.tecsisa.com/")),
      homepage := Some(url("https://github.com/Tecsisa/lightQL")),
      scmInfo := Some(
        ScmInfo(url("https://github.com/Tecsisa/lightQL"), "git@github.com:Tecsisa/lightQL.git")
      ),
      developers += Developer(
        "contributors",
        "Contributors",
        "",
        url("https://github.com/Tecsisa/lightQL/graphs/contributors")),
      pomIncludeRepository := (_ => false),
      licenses := Seq(("Apache-2.0", url("https://www.apache.org/licenses/LICENSE-2.0.txt"))),
      scalaVersion := crossScalaVersions.value.head,
      version in ThisBuild := Version.ElasticMaterializer,
      crossScalaVersions := Version.ScalaVersions,
      crossVersion := CrossVersion.binary,
      publishTo := sonatypePublishToBundle.value,
      scalacOptions ++=
        Seq("-encoding",
          "UTF-8",
          "-unchecked",
          "-deprecation",
          "-Xlint") ++
          (CrossVersion.partialVersion(scalaVersion.value) match {
            case Some((2, n)) if n >= 13 => Seq(
              "-Xsource:3",
              "-Wdead-code"
            )
            case _ => Seq(
              "-Yno-adapted-args",
              "-Ywarn-dead-code",
              "-Xfuture" // prevents of future breaking changes
            )
          }),
      scalacOptions in (Compile, console) ~= (_.filterNot(
        Set(
          "-Xfatal-warnings",
          "-Xlint"
        ))),
      scalacOptions in (Test, console) ~= (_.filterNot(
        Set(
          "-Xfatal-warnings",
          "-Xlint"
        ))),
      javacOptions ++= Seq(
        "-Xlint:unchecked"
      ),
      // show full stack traces and test case durations
      testOptions in Test += Tests.Argument("-oDF"),
      headerMappings := headerMappings.value ++ Map(
        FileType.scala -> CommentStyle.cStyleBlockComment
      ),
      headerLicense := headerLic,
      // @see
      // http://stackoverflow.com/questions/26940253/in-sbt-how-do-you-override-scalacoptions-for-console-in-all-configurations
      scalacOptions in (Compile, console) ~= (_ filterNot (_ == "-Ywarn-unused-import")),
      scalacOptions in (Test, console) := (scalacOptions in (Compile, console)).value,
      unmanagedSourceDirectories.in(Compile) := Vector(scalaSource.in(Compile).value),
      unmanagedSourceDirectories.in(Test) := Vector(scalaSource.in(Test).value),
      // Additional resolvers
      resolvers ++= Seq(
        Resolver.sonatypeRepo("releases"),
        "jgit-repo" at "https://download.eclipse.org/jgit/maven" // needed by tut
      ),
      // Scalafmt settings
      scalafmtOnCompile in ThisBuild := false
    )
}

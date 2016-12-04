import sbt._
import sbt.Keys._
import sbt.plugins.JvmPlugin
import de.heikoseeberger.sbtheader._
import de.heikoseeberger.sbtheader.HeaderPlugin.autoImport._
import com.typesafe.sbt.GitPlugin
import org.scalafmt.sbt.ScalaFmtPlugin.autoImport._

object Common extends AutoPlugin {



  final val FileHeader = (HeaderPattern.cStyleBlockComment,
    """|/*
       | * Copyright (C) 2016 TECNOLOGIA, SISTEMAS Y APLICACIONES S.L. <http://www.tecsisa.com>
       | */
       | """.stripMargin)

  override def requires = JvmPlugin && GitPlugin && HeaderPlugin

  override def trigger = allRequirements

  override def projectSettings = reformatOnCompileSettings ++ Seq(
    organization := "com.tecsisa",
    organizationName := "Tecnología, Sistemas y Aplicaciones S.L.",
    organizationHomepage := Some(url("http://www.tecsisa.com/")),
    homepage := Some(url("https://github.com/Tecsisa/lightQL")),
    scmInfo := Some(ScmInfo(url("https://github.com/Tecsisa/lightQL"), "git@github.com:Tecsisa/lightQL.git")),
    developers += Developer("contributors", "Contributors", "", url("https://github.com/Tecsisa/lightQL/graphs/contributors")),
    pomIncludeRepository := (_ => false),
    licenses := Seq(("Apache-2.0", url("http://www.apache.org/licenses/LICENSE-2.0"))),

    scalaVersion := crossScalaVersions.value.head,
    crossScalaVersions := Version.ScalaVersions,
    crossVersion := CrossVersion.binary,

    scalacOptions ++= Seq(
      "-encoding", "UTF-8",
      "-unchecked",
      "-deprecation",
      "-Xlint",
      "-Yno-adapted-args",
      "-Ywarn-dead-code",
      "-Ywarn-unused-import", // only 2.11
      "-Xfuture" // prevents of future breaking changes
    ),

    javacOptions ++= Seq(
      "-Xlint:unchecked"
    ),

    // show full stack traces and test case durations
    testOptions in Test += Tests.Argument("-oDF"),

    headers := headers.value ++ Map("scala" -> FileHeader),

    // @see
    // http://stackoverflow.com/questions/26940253/in-sbt-how-do-you-override-scalacoptions-for-console-in-all-configurations
    scalacOptions in (Compile, console) ~= (_ filterNot (_ == "-Ywarn-unused-import")),
    scalacOptions in (Test, console) := (scalacOptions in (Compile, console)).value,
    unmanagedSourceDirectories.in(Compile) := Vector(scalaSource.in(Compile).value),
    unmanagedSourceDirectories.in(Test) := Vector(scalaSource.in(Test).value),

    ivyScala := ivyScala.value.map(_.copy(overrideScalaVersion = sbtPlugin.value)), // TODO Remove once this workaround no longer needed (https://github.com/sbt/sbt/issues/2786)!

    // Scalafmt settings
    formatSbtFiles := false,
    scalafmtConfig := Some(baseDirectory.in(ThisBuild).value / ".scalafmt.conf"),

    // Additional resolvers
    resolvers ++= Seq(
      Resolver.sonatypeRepo("releases"),
      "jgit-repo" at "http://download.eclipse.org/jgit/maven" // needed by tut
    )
  )
}

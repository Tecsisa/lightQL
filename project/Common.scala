import sbt._
import sbt.Keys._
import sbt.plugins.JvmPlugin
import com.typesafe.sbt.GitPlugin
import org.scalafmt.sbt.ScalaFmtPlugin.autoImport._

object Common extends AutoPlugin {

  override def requires = JvmPlugin && GitPlugin

  override def trigger = allRequirements

  override def projectSettings = reformatOnCompileSettings ++ Seq(
    organization := "com.tecsisa",
    organizationName := "Tecnología, Sistemas y Aplicaciones S.L.",
    organizationHomepage := Some(url("http://www.tecsisa.com/")),
    scalaVersion := Version.Scala,
    crossScalaVersions := Seq(scalaVersion.value),
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

    // @see
    // http://stackoverflow.com/questions/26940253/in-sbt-how-do-you-override-scalacoptions-for-console-in-all-configurations
    scalacOptions in (Compile, console) ~= (_ filterNot (_ == "-Ywarn-unused-import")),
    scalacOptions in (Test, console) := (scalacOptions in (Compile, console)).value,
    unmanagedSourceDirectories.in(Compile) := Vector(scalaSource.in(Compile).value),
    unmanagedSourceDirectories.in(Test) := Vector(scalaSource.in(Test).value),

    ivyScala := ivyScala.value.map(_.copy(overrideScalaVersion = sbtPlugin.value)), // TODO Remove once this workaround no longer needed (https://github.com/sbt/sbt/issues/2786)!

    // Scalafmt settings
    formatSbtFiles := false,
    scalafmtConfig := Some(baseDirectory.in(ThisBuild).value / ".scalafmt.conf")
  )
}

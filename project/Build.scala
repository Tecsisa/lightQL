import com.typesafe.sbt.GitPlugin
import org.scalafmt.sbt.ScalaFmtPlugin
import sbt.Keys._
import sbt._
import sbt.plugins.JvmPlugin

object Build extends AutoPlugin {

  override def requires = JvmPlugin && GitPlugin

  override def trigger = allRequirements

  override def projectSettings = Vector(
    // Core settings
    organization := "com.tecsisa.wr",
    scalaVersion := Version.Scala,
    crossScalaVersions := Vector(scalaVersion.value),
    scalacOptions ++= Vector(
      "-unchecked",
      "-deprecation",
      "-language:_",
      "-target:jvm-1.8",
      "-encoding", "UTF-8",
      "-Ywarn-unused-import"
    ),
    scalacOptions in (Compile, console) ~= (_ filterNot (_ == "-Ywarn-unused-import")),
    scalacOptions in (Test, console) := (scalacOptions in (Compile, console)).value,
    unmanagedSourceDirectories.in(Compile) := Vector(scalaSource.in(Compile).value),
    unmanagedSourceDirectories.in(Test) := Vector(scalaSource.in(Test).value),

    // Scalafmt settings
    ScalaFmtPlugin.autoImport.scalafmtConfig := Some(file(".scalafmt")),

    // Git settings
    GitPlugin.autoImport.git.useGitDescribe := true
  )
}

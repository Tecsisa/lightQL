import com.typesafe.sbt.{ GitPlugin, SbtScalariform }
import sbt._
import sbt.plugins.JvmPlugin
import sbt.Keys._
import scalariform.formatter.preferences.{ AlignSingleLineCaseStatements, DoubleIndentClassDeclaration }

object Build extends AutoPlugin {

  override def requires = JvmPlugin && GitPlugin && SbtScalariform

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
      "-encoding", "UTF-8"
    ),
    unmanagedSourceDirectories.in(Compile) := Vector(scalaSource.in(Compile).value),
    unmanagedSourceDirectories.in(Test) := Vector(scalaSource.in(Test).value),

    // Scalariform settings
    SbtScalariform.autoImport.scalariformPreferences := SbtScalariform.autoImport.scalariformPreferences.value
      .setPreference(AlignSingleLineCaseStatements, true)
      .setPreference(AlignSingleLineCaseStatements.MaxArrowIndent, 100)
      .setPreference(DoubleIndentClassDeclaration, true),

    // Git settings
    GitPlugin.autoImport.git.useGitDescribe := true
  )
}

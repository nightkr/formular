import com.lihaoyi.workbench.Plugin._
import org.scalajs.sbtplugin.cross.CrossProject

lazy val root = project.in(file("."))
  .aggregate(
    coreJVM, coreJS,
    reactJVM, reactJS,
    exampleJVM, exampleJS
  )

lazy val core = crossProject
  .settings(
    libraryDependencies ++= Seq(
      "com.github.benhutchison" %%% "prickle" % "1.1.5"
    )
  )

lazy val coreJVM = core.jvm

lazy val coreJS = core.js

lazy val react = crossProject.dependsOn(core: CrossProject)
  .jsSettings(
    libraryDependencies ++= Seq(
      "com.github.japgolly.scalajs-react" %%% "core" % "0.9.0",
      "com.github.japgolly.scalajs-react" %%% "ext-monocle" % "0.9.0"
    )
  )

lazy val reactJVM = react.jvm

lazy val reactJS = react.js

lazy val example = crossProject.dependsOn(react: CrossProject)
  .jsSettings(workbenchSettings: _*)
  .jsSettings(
    libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "0.8.1",
    jsDependencies += "org.webjars" % "react" % "0.13.3" / "react-with-addons.js" commonJSName "React",
    bootSnippet := "location.reload()",
    updateBrowsers <<= updateBrowsers.triggeredBy(fastOptJS in Compile)
  )

lazy val exampleJVM = example.jvm

lazy val exampleJS = example.js

scalaVersion in ThisBuild := "2.11.6"

name := "formular"

organization := "se.nullable.formular"

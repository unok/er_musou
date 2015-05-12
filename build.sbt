import play.twirl.sbt.SbtTwirl
import play.twirl.sbt.Import.TwirlKeys

version := "0.1-SNAPSHOT"

sbtPlugin := true

scalaVersion := "2.11.6"

scalacOptions in Test ++= Seq("-Yrangepos")

libraryDependencies += "org.scala-lang.modules" %% "scala-parser-combinators" % "1.0.2"

libraryDependencies += "com.typesafe" % "config" % "1.2.1"

ScctPlugin.instrumentSettings

SbtTwirl.projectSettings

TwirlKeys.templateImports in Compile ++= Seq("jp.ne.suehiro.ErMusou")

libraryDependencies ++= Seq(
  "org.specs2" %% "specs2-core" % "2.4.17" % "test",
  "org.apache.commons" % "commons-lang3" % "3.3.2",
  "commons-io" % "commons-io" % "2.4"
)

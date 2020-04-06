organization := "je.jia"

version := "1.0-SNAPSHOT"

name := "fpnew"

scalaVersion := "2.12.10"

scalacOptions ++= Seq("-Xsource:2.11")

libraryDependencies ++= Seq(
    "edu.berkeley.cs" %% "chisel3" % "3.2.+",
    "edu.berkeley.cs" %% "chisel-iotesters" % "1.3.+",
)

addCompilerPlugin(
"org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full
)
ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.1.3"

lazy val root = (project in file("."))
  .settings(
    name := "Games"
  )

libraryDependencies += "org.scalafx" %% "scalafx" % "18.0.1-R28"

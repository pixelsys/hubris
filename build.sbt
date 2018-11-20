name := "enx-hubris"
version := "0.1-SNAPSHOT"
scalaVersion := "2.12.7"
//EclipseKeys.withSource := true
libraryDependencies ++= Seq(
  "org.scalanlp" %% "breeze" % "1.0-RC2",
  "org.scalatest" %% "scalatest" % "3.2.0-SNAP10" % Test
)

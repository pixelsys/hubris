name := "enx-hubris"
version := "0.1-SNAPSHOT"
scalaVersion := "2.11.7"
EclipseKeys.withSource := true
libraryDependencies ++= Seq(
  "org.scalanlp" %% "breeze" % "0.11.2",
  "org.scalatest" % "scalatest_2.11" % "2.2.4" % "test"
)

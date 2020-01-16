name := "enx-hubris"
version := "0.1-SNAPSHOT"
javacOptions ++= Seq("-source", "11")
scalaVersion := "2.13.1"
//EclipseKeys.withSource := true
libraryDependencies ++= Seq(
  "org.projectlombok" % "lombok" % "1.18.10",
  "junit" % "junit" % "4.13" % Test,
  "com.novocode" % "junit-interface" % "0.11" % Test,
  "org.scalanlp" %% "breeze" % "1.0",
  "org.scalatest" %% "scalatest" % "3.1.0" % Test,
  "org.scalacheck" %% "scalacheck" % "1.14.0" % Test
)

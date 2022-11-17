val root = (project in file("."))

scalaVersion := "3.2.1"

libraryDependencies ++= Seq(
  "org.reactivemongo" %% "reactivemongo" % "1.1.0-RC6"
  /* "org.reactivemongo" %% "reactivemongo" % "1.1.0-RC6-SNAPSHOT", */
  /* "org.reactivemongo" %% "reactivemongo-bson-api" % "1.1.0-RC6" */
)

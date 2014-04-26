organization := "com.ivan.nikolov"

name := "json-reformatter"

scalaVersion := "2.10.3"

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

javaOptions ++= Seq("-target", "1.7", "-source", "1.7")

publishMavenStyle := true

libraryDependencies ++= {
  Seq(
    "com.fasterxml.jackson.module" %%	"jackson-module-scala" %	"2.3.3",
    "de.grundid.opendatalab" % "geojson-jackson" % "1.1"
  )
}
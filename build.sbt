name := "SPWS"

version := "0.1"

scalaVersion := "2.13.10"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.6.20",
  "org.jsoup" % "jsoup" % "1.14.3",
  "org.apache.spark" %% "spark-core" % "3.4.0",
  "org.apache.spark" %% "spark-sql" % "3.4.0",
  "org.scalatest" %% "scalatest" % "3.2.15" % Test,
  "org.openjdk.jmh" % "jmh-core" % "1.36",
  "org.openjdk.jmh" % "jmh-generator-annprocess" % "1.36"
)

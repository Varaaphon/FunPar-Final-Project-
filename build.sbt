lazy val root = (project in file("."))
  .settings(
    name := "ParallelWebScraper",
    version := "0.1",
    scalaVersion := "2.13.12",
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-actor" % "2.6.19",
      "com.typesafe.akka" %% "akka-stream" % "2.6.19",
      "org.jsoup" % "jsoup" % "1.14.3",
      "ch.qos.logback" % "logback-classic" % "1.2.6",
      "com.typesafe.play" %% "play-json" % "2.9.2",
    ),
    mainClass in Compile := Some("ParallelWebScraper")
  )

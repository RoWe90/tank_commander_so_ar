lazy val commonSettings = Seq(
  name := "TankCommander",
  version := "0.0.1",
  scalaVersion := "2.12.4"
)

lazy val root = (project in file(".")).settings(
  name := "TankCommander",
  libraryDependencies ++= commonDependencies,
).aggregate(
  gridModule,
  playerModule,
  uiControllerModule
)

lazy val uiControllerModule = project.settings(
  commonSettings,
  name := "UIControllerModule",
  libraryDependencies ++= commonDependencies,
).dependsOn(gridModule).aggregate(gridModule)

lazy val gridModule = project.settings(
  commonSettings,
  name := "GridModule",
  libraryDependencies ++= commonDependencies,
).dependsOn(playerModule).aggregate(playerModule)

// Player
lazy val playerModule = project.settings(
  commonSettings,
  name := "PlayerModule",
  libraryDependencies ++= commonDependencies,
)

val commonDependencies = Seq(
  "org.scala-lang.modules" % "scala-swing_2.12" % "2.0.1",
  "com.google.inject" % "guice" % "4.1.0",
  "net.codingwell" %% "scala-guice" % "4.1.0",
  "org.scala-lang.modules" % "scala-xml_2.12" % "1.0.6",
  "com.typesafe.play" %% "play-json" % "2.6.6",
  "net.liftweb" %% "lift-json" % "3.0.1",
  "org.scalatest" %% "scalatest" % "3.0.5" % "test",
  "junit" % "junit" % "4.8" % "test",
  "com.typesafe.akka" %% "akka-http"   % "10.1.12",
  "com.typesafe.akka" %% "akka-stream" % "2.6.5"
)





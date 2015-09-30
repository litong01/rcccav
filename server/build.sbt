name := """rcccav"""

version := "1.0"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.6"

libraryDependencies ++= Seq(
  cache,
  javaWs
)

routesGenerator := InjectedRoutesGenerator

import com.typesafe.sbt.packager.archetypes.ServerLoader
enablePlugins(JavaServerAppPackaging)
mainClass in Compile := Some("play.core.server.ProdServerStart")
serverLoading in Debian := ServerLoader.Upstart
maintainer in Linux := "Tong Li <email4tong@yahoo.com>"
packageSummary in Linux := "RCCC AV Project"
packageDescription := "Raleigh Chinese Christian Church AV project"
daemonUser in Linux := "root"
daemonGroup in Linux := "root"

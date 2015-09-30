name := """rcccav"""

version := "1.0"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.6"

libraryDependencies ++= Seq(
  cache,
  javaWs
)

// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.
routesGenerator := InjectedRoutesGenerator

import com.typesafe.sbt.packager.archetypes.ServerLoader
enablePlugins(JavaServerAppPackaging)
mainClass in Compile := Some("empty")
serverLoading in Debian := ServerLoader.Upstart
maintainer in Linux := "Tong Li <email4tong@yahoo.com>"
packageSummary in Linux := "RCCC AV Project"
packageDescription := "Raleigh Chinese Christian Church AV project"
daemonUser in Linux := "root"
daemonGroup in Linux := "root"

TaskKey[Unit]("check-control-files") <<= (target, streams) map { (target, out) =>
  val debian = target / "rcccav-1.0" / "DEBIAN"
  val postinst = IO.read(debian / "postinst")
  val postrm = IO.read(debian / "postrm")
  assert(postinst contains """addGroup daemongroup """"", "postinst misses addgroup for daemongroup: " + postinst)
  assert(postinst contains """addUser daemonuser "" daemongroup "debian-test user-daemon" "/bin/false"""", "postinst misses useradd for daemonuser: " + postinst)
  assert(postinst contains "chown daemonuser:daemongroup /var/log/debian-test", "postinst misses chown daemonuser /var/log/debian-test: " + postinst)
  assert(!(postinst contains "addgroup --system daemonuser"), "postinst has addgroup for daemonuser: " + postinst)
  assert(!(postinst contains "useradd --system --no-create-home --gid daemonuser --shell /bin/false daemonuser"), "postinst has useradd for daemongroup: " + postinst)
  assert(postrm contains "deleteUser daemonuser", "postrm misses purging daemonuser user: " + postrm)
  assert(postrm contains "deleteGroup daemongroup", "postrm misses purging daemongroup group: " + postrm)
  out.log.success("Successfully tested upstart control files")
  ()
}
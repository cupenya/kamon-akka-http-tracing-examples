lazy val lib = (project in file("./kamon-akka-example-lib"))
  .settings(commonSettings)

lazy val root = (project in file("."))
  .dependsOn(lib)
  .settings(Seq(name := "kamon-akka-http-tracing-examples"))
  .settings(serviceSettings)
  .settings(commonSettings)
  .enablePlugins(JavaServerAppPackaging, DockerPlugin, JavaAgent)

val shortCommit = ("git rev-parse --short HEAD" !!).replaceAll("\\n", "").replaceAll("\\r", "")

lazy val commonSettings = Seq(
  version := s"1.0.0-${shortCommit}",
  scalaVersion := "2.11.8",
  scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8"),
  resolvers += Resolver.bintrayRepo("kamon-io", "snapshots")
)

lazy val serviceSettings = Seq(
  javaOptions in reStart <++= AspectjKeys.weaverOptions in Aspectj,
  packageName in Docker := "elmarweber/" + name.value,
  dockerBaseImage := "airdock/oracle-jdk:jdk-1.8",
  javaAgents += "org.aspectj" % "aspectjweaver" % "1.8.10"
) ++ aspectjSettings




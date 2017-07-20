lazy val lib = (project in file("./kamon-akka-example-lib"))
  .settings(commonSettings)

lazy val auth = (project in file("./kamon-akka-example-auth"))
  .dependsOn(lib)
  .settings(commonSettings)
  .settings(serviceSettings)
  .enablePlugins(JavaServerAppPackaging, DockerPlugin, JavaAgent)

lazy val profile = (project in file("./kamon-akka-example-profile"))
  .dependsOn(lib)
  .settings(commonSettings)
  .settings(serviceSettings)
  .enablePlugins(JavaServerAppPackaging, DockerPlugin, JavaAgent)

lazy val gateway = (project in file("./kamon-akka-example-gateway"))
  .dependsOn(lib)
  .settings(commonSettings)
  .settings(serviceSettings)
  .enablePlugins(JavaServerAppPackaging, DockerPlugin, JavaAgent)

lazy val coolStuff = (project in file("./kamon-akka-example-cool-stuff"))
  .dependsOn(lib)
  .settings(commonSettings)
  .settings(serviceSettings)
  .enablePlugins(JavaServerAppPackaging, DockerPlugin, JavaAgent)

lazy val analyticsPipeline = (project in file("./kamon-akka-example-analytics-pipeline"))
  .dependsOn(lib)
  .settings(commonSettings)
  .settings(serviceSettings)
  .enablePlugins(JavaServerAppPackaging, DockerPlugin, JavaAgent)

lazy val root = (project in file("."))
  .dependsOn(lib)
  .aggregate(auth, profile, gateway, coolStuff, analyticsPipeline)
  .settings(Seq(name := "kamon-akka-http-tracing-examples-root"))
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




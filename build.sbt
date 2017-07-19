name := "akka-http-microservice"

organization := "org.elmarweber.github"

maintainer := "Elmar Weber"


val shortCommit = ("git rev-parse --short HEAD" !!).replaceAll("\\n", "").replaceAll("\\r", "")


version := s"1.0.0-${shortCommit}"

scalaVersion := "2.11.8"

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

resolvers += Resolver.bintrayRepo("kamon-io", "snapshots")

libraryDependencies ++= {
  val akkaV       = "2.4.19"
  val akkaHttpV   = "10.0.9"
  val kamonV      = "0.6.7"
  val scalaTestV  = "3.0.1"
  Seq(
    "com.typesafe.akka" %% "akka-actor" % akkaV,
    "com.typesafe.akka" %% "akka-stream" % akkaV,
    "com.typesafe.akka" %% "akka-testkit" % akkaV,
    "com.typesafe.akka" %% "akka-http" % akkaHttpV,
    "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpV,
    "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpV,
    "io.kamon"          %% "kamon-core" % "1.0.0-RC1-5ee1b830aff118dc264399ddfb92c1f2b1f51a85",
    "io.kamon"          %% "kamon-scala" % "1.0.0-RC1-2666212583916c0db25d3a72d19e5038a79de139",
    "io.kamon"          %% "kamon-executors" % "1.0.0-RC1-5d6a5ebffba5eea7933b2d40808136a878bb15b0",
  //  "io.kamon"          %% "kamon-akka-2.4" % "1.0.0-RC1-5472bca942c01bb87720263b36978cc0b243365e",
    "io.kamon"          %% "kamon-jaeger" % "1.0.0-RC1-18177050418884ccad1f3a23f433fcde80733c82",
    "com.typesafe.scala-logging" %% "scala-logging" % "3.7.1",
    "org.apache.commons" % "commons-lang3" % "3.4",
    "ch.qos.logback" % "logback-classic" % "1.2.3",
    "org.scalatest"     %% "scalatest" % scalaTestV % "test"
  )
}

aspectjSettings

javaOptions in reStart <++= AspectjKeys.weaverOptions in Aspectj


// docker
enablePlugins(JavaServerAppPackaging)
enablePlugins(DockerPlugin)

packageName in Docker := "cupenya/" + name.value
dockerBaseImage := "airdock/oracle-jdk:jdk-1.8"

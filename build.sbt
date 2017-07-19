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
    "io.kamon"          %% "kamon-core" % "1.0.0-RC1-450978b92bc968bfdb9c6470028ad30586433609",
    "io.kamon"          %% "kamon-scala" % "1.0.0-RC1-a815637c51be4158a576a4a811050f4470edadf4",
    "io.kamon"          %% "kamon-jaeger" % "1.0.0-RC1-9eec74a0c7f4332336928431852104cc9ad19373",
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

name := "akka-http-microservice"

organization := "org.elmarweber.github"

version := "1.0.0"

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
    "io.kamon"          %% "kamon-scala" % "1.0.0-RC1-933bb552dab8c322a30363f8a56a4e66274367ce",
    "io.kamon"          %% "kamon-executors" % "1.0.0-RC1-5d6a5ebffba5eea7933b2d40808136a878bb15b0",
    "io.kamon"          %% "kamon-akka-2.4" % "1.0.0-RC1-5472bca942c01bb87720263b36978cc0b243365e",
    "io.kamon"          %% "kamon-jaeger" % "1.0.0-RC1-6cbd74406aac0bedc20746abc78f27e566de1f90",
    "com.typesafe.scala-logging" %% "scala-logging" % "3.7.1",
    "org.apache.commons" % "commons-lang3" % "3.4",
    "ch.qos.logback" % "logback-classic" % "1.2.3",
    "org.scalatest"     %% "scalatest" % scalaTestV % "test"
  )
}

aspectjSettings

javaOptions in reStart <++= AspectjKeys.weaverOptions in Aspectj



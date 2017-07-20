package org.elmarweber.github

import akka.actor._
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.typesafe.scalalogging.StrictLogging
import kamon.Kamon
import kamon.jaeger.Jaeger
import org.elmarweber.github.kate.lib.api.AuthHttpApiClient
import org.elmarweber.github.kate.lib.httpclient.HttpClient
import org.elmarweber.github.kate.lib.kamon.LogSpanReporter

object Boot extends App with ServiceRoute with StrictLogging {
  Kamon.addReporter(new LogSpanReporter())
  Kamon.addReporter(new Jaeger())

  implicit val system = ActorSystem()
  implicit val ec = system.dispatcher
  implicit val materializer = ActorMaterializer()
  //Kamon.tracer.subscribe(system.actorOf(Props(new TraceCollectorActor())))
  implicit val api = new EchoSubServiceHttpClient(HttpClient.fromEndpoint(Configuration.service.client.endpoint).build())
  val authApi = new AuthHttpApiClient(HttpClient.fromEndpoint(Configuration.service.auth.endpoint).build())

  
  Http().bindAndHandle(serviceRoute, Configuration.service.http.interface, Configuration.service.http.port).transform(
    binding => logger.info(s"REST interface bound to ${binding.localAddress} "), { t => logger.error(s"Couldn't bind interface: ${t.getMessage}", t); sys.exit(1) }
  )
}

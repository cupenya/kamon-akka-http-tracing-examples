package org.elmarweber.github

import akka.actor._
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.StrictLogging
import kamon.Kamon
import kamon.jaeger.Jaeger
import org.elmarweber.github.httpclient.HttpClient

object Boot extends App with ServiceRoute with StrictLogging {
  Kamon.addReporter(new MySpanReporter())
  Kamon.addReporter(new Jaeger())

  implicit val system = ActorSystem()
  implicit val ec = system.dispatcher
  implicit val materializer = ActorMaterializer()
  //Kamon.tracer.subscribe(system.actorOf(Props(new TraceCollectorActor())))
  implicit val api = new EchoSubServiceHttpClient(HttpClient.fromEndpoint(Configuration.service.client.endpoint).build())

  
  Http().bindAndHandle(serviceRoute, Configuration.service.http.interface, Configuration.service.http.port).transform(
    binding => logger.info(s"REST interface bound to ${binding.localAddress} "), { t => logger.error(s"Couldn't bind interface: ${t.getMessage}", t); sys.exit(1) }
  )
}

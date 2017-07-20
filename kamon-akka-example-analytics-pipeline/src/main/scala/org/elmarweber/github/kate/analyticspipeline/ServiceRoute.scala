package org.elmarweber.github.kate.analyticspipeline

import akka.actor.ActorRef
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.server.Directives
import com.typesafe.scalalogging.StrictLogging
import kamon.Kamon
import org.elmarweber.github.kate.lib.api.{AnalyticsEvent, AuthRequest, AuthResponse}
import org.elmarweber.github.kate.lib.kamon.RouteLoggingDirective
import spray.json._

import scala.concurrent.{ExecutionContext, Future}


trait ServiceRoute extends Directives with RouteLoggingDirective with StrictLogging with DefaultJsonProtocol {
  implicit def ec: ExecutionContext


  def streamSourceActor: ActorRef

  val serviceRoute = pathPrefix("api") {
    trace {
      pathPrefix("logEvent") {
        pathEndOrSingleSlash {
          post {
            entity(as[AnalyticsEvent]) { event =>
              complete {
                logger.info(s"Queued event ${event}")
                streamSourceActor ! (event, Kamon.tracer.activeSpan().context)
                Future.successful(JsNumber(1))
              }
            }
          }
        }
      }
    }
  }
}




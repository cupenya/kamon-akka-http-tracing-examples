package org.elmarweber.github.kate.analyticspipeline

import akka.actor.ActorRef
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.server.Directives
import org.elmarweber.github.kate.lib.api.{AnalyticsEvent, AuthRequest, AuthResponse}
import org.elmarweber.github.kate.lib.kamon.RouteLoggingDirective

import scala.concurrent.ExecutionContext


trait ServiceRoute extends Directives with RouteLoggingDirective {
  implicit def ec: ExecutionContext


  def streamSourceActor: ActorRef

  val serviceRoute = pathPrefix("api") {
    trace {
      pathPrefix("logEvent") {
        pathEndOrSingleSlash {
          post {
            entity(as[AnalyticsEvent]) { event =>
              complete {
                streamSourceActor ! event
                "OK"
              }
            }
          }
        }
      }
    }
  }
}




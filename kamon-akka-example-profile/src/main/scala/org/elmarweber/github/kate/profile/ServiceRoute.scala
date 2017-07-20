package org.elmarweber.github.kate.profile

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.server.Directives
import org.elmarweber.github.kate.lib.api.{AuthRequest, AuthResponse}
import org.elmarweber.github.kate.lib.kamon.RouteLoggingDirective

import scala.concurrent.ExecutionContext


trait ServiceRoute extends Directives with RouteLoggingDirective {
  implicit def ec: ExecutionContext

  val serviceRoute = pathPrefix("api") {
    trace {
      pathPrefix("profiles" / Segment) { id =>
        pathEndOrSingleSlash {
          get {
            complete {
              ProfileService.get(id)
            }
          }
        }
      }
    }
  }
}




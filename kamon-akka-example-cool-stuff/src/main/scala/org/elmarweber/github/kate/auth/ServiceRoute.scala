package org.elmarweber.github.kate.auth

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.server.Directives
import org.elmarweber.github.kate.lib.api.{AuthRequest, AuthResponse}
import org.elmarweber.github.kate.lib.kamon.RouteLoggingDirective

import scala.concurrent.ExecutionContext


trait ServiceRoute extends Directives with RouteLoggingDirective with CoolStuffService {
  implicit def ec: ExecutionContext

  val serviceRoute = pathPrefix("api") {
    trace {
      pathPrefix("cool-stuff" / "do") {
        pathEndOrSingleSlash {
          get {
            complete {   implicit val authResponse = AuthResponse("asd", "asd")
              doCoolStuff()
            }
          }
        }
      }
    }
  }
}




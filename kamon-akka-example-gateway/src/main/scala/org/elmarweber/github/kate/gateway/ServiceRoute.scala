package org.elmarweber.github.kate.gateway

import akka.http.scaladsl.server.Directives
import org.elmarweber.github.kate.lib.api.CoolStuffApi
import org.elmarweber.github.kate.lib.kamon.RouteLoggingDirective
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._

import scala.concurrent.ExecutionContext


trait ServiceRoute extends Directives with RouteLoggingDirective with AuthorizationDirectives {
  implicit def ec: ExecutionContext

  def coolStuffApi: CoolStuffApi

  val serviceRoute = pathPrefix("api") {
    trace {
      authorized { auth =>
        pathPrefix("cool-stuff" / "do") {
          pathEndOrSingleSlash {
            get {
              complete {
                coolStuffApi.doCoolStuff()
              }
            }
          }
        }
      }
    }
  }
}




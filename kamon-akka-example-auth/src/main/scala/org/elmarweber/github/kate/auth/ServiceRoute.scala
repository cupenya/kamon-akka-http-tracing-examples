package org.elmarweber.github.kate.auth

import akka.http.scaladsl.client.RequestBuilding
import akka.http.scaladsl.model.Uri.Query
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives
import kamon.Kamon
import kamon.util.CallingThreadExecutionContext
import org.apache.commons.codec.digest.DigestUtils
import org.elmarweber.github.kate.lib.api.{AuthRequest, AuthResponse}
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import com.typesafe.scalalogging.StrictLogging
import kamon.trace.Span
import org.elmarweber.github.kate.lib.kamon.{InstrumentationSupport, RouteLoggingDirective}

import scala.concurrent.{ExecutionContext, Future}


trait ServiceRoute extends Directives with RouteLoggingDirective {
  implicit def ec: ExecutionContext

  val serviceRoute = pathPrefix("api") {
    trace {
      pathPrefix("auth") {
        pathEndOrSingleSlash {
          post {
            entity(as[AuthRequest]) { authRequest =>
              complete {
                AuthService.doAuth(authRequest.apiKey).map { token =>
                  AuthResponse(authRequest.apiKey, token)
                }
              }
            }
          }
        }
      }
    }
  }
}

trait AuthService extends InstrumentationSupport with StrictLogging {
  def doAuth(apiKey: String)(implicit ec: ExecutionContext): Future[String] = traceFuture("doAuth") {
    Future {
      Kamon.tracer.activeSpan().addSpanTag("apiKey", apiKey)
      if (apiKey == "") {
        Kamon.tracer.activeSpan().annotate("error", Map("message" -> "Empty API Key provided"))
        logger.error(s"Empty API Key provided")
        throw new Error("Empty API Key provided")
      } else {
        Thread.sleep(50)
        logger.info(s"Successfully authenticated ${apiKey}")
        DigestUtils.sha256Hex(apiKey + "secret")
      }

    }
  }
}

object AuthService extends AuthService
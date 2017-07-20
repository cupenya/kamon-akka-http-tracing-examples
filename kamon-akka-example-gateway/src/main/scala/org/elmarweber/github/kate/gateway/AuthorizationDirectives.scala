package org.elmarweber.github.kate.gateway

import akka.http.scaladsl.server.{AuthorizationFailedRejection, Directive1, Directives}
import com.typesafe.scalalogging.StrictLogging
import kamon.Kamon
import org.elmarweber.github.kate.lib.api.{AuthApi, AuthRequest, AuthResponse}

import scala.concurrent.ExecutionContext


trait AuthorizationDirectives extends StrictLogging {
  self: Directives =>

  implicit def ec: ExecutionContext
  def authApi: AuthApi

  def authorized: Directive1[AuthResponse] = {
    optionalHeaderValueByName("x-api-key").flatMap {
      case Some(apiKey) =>
        onComplete(authApi.doAuth(AuthRequest(apiKey))).flatMap { authTry =>
          authTry.map(provide)
            .recover {
              case ex =>
                logger.error("err", ex)
                Kamon.tracer.activeSpan().annotate("error", Map("message" -> s"Auth failed for apiKey ${apiKey}"))
                reject(AuthorizationFailedRejection).toDirective[Tuple1[AuthResponse]]
            }
            .get
        }
      case None =>
        reject(AuthorizationFailedRejection)
    }
  }
}


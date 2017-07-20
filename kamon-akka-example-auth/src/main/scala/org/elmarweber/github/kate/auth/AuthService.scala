package org.elmarweber.github.kate.auth

import com.typesafe.scalalogging.StrictLogging
import kamon.Kamon
import org.apache.commons.codec.digest.DigestUtils
import org.elmarweber.github.kate.lib.kamon.InstrumentationSupport

import scala.concurrent.{ExecutionContext, Future}

trait AuthService extends InstrumentationSupport with StrictLogging {
  def doAuth(apiKey: String)(implicit ec: ExecutionContext): Future[String] = traceFuture("doAuth") {
    Future {
      logger.debug(s"Trying to authenticate ${apiKey}")
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
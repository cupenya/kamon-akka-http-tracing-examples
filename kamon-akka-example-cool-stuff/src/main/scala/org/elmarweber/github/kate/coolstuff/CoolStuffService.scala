package org.elmarweber.github.kate.coolstuff

import com.typesafe.scalalogging.StrictLogging
import org.elmarweber.github.kate.lib.api._
import org.elmarweber.github.kate.lib.kamon.InstrumentationSupport

import scala.concurrent.{ExecutionContext, Future}

trait CoolStuffService extends InstrumentationSupport with StrictLogging {
  def profileApi: ProfileApi
  def analyticsPipelineApi: AnalyticsPipelineApi

  def doCoolStuff()(implicit ec: ExecutionContext, auth: AuthResponse): Future[CoolStuffResponse] = traceFuture("doCoolStuff") {
    logger.debug("Doing really cool stuff ")
    for {
      profile <- profileApi.get(auth.userId)
      event = AnalyticsEvent(auth.userId, profile.country, "cool-event")
      _ <- analyticsPipelineApi.logEvent(event)
    } yield {
      CoolStuffResponse("Hello " + profile.name)
    }
  }
}

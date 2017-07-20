package org.elmarweber.github.kate.auth

import com.typesafe.scalalogging.StrictLogging
import org.elmarweber.github.kate.lib.api.{AuthResponse, CoolStuffResponse, ProfileApi}
import org.elmarweber.github.kate.lib.kamon.InstrumentationSupport

import scala.concurrent.{ExecutionContext, Future}

trait CoolStuffService extends InstrumentationSupport with StrictLogging {
  def profileApi: ProfileApi

  def doCoolStuff()(implicit ec: ExecutionContext, auth: AuthResponse): Future[CoolStuffResponse] = traceFuture("doCoolStuff") {
    for {
      profile <- profileApi.get(auth.userId)
    } yield {
      CoolStuffResponse("Hello " + profile.name)
    }
  }
}

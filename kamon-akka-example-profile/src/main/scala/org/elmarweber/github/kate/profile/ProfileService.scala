package org.elmarweber.github.kate.profile

import com.typesafe.scalalogging.StrictLogging
import kamon.Kamon
import org.apache.commons.codec.digest.DigestUtils
import org.elmarweber.github.kate.lib.api.UserProfile
import org.elmarweber.github.kate.lib.kamon.InstrumentationSupport

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Random

trait ProfileService extends InstrumentationSupport with StrictLogging {
  def get(id: String)(implicit ec: ExecutionContext): Future[UserProfile] = traceFuture("get") {
    Future {
      Kamon.tracer.activeSpan().addSpanTag("userId", id)
      Thread.sleep(30 + Random.nextInt(30))
      UserProfile(id, "Marcus Cole", "Netherlands")
    }
  }
}

object ProfileService extends ProfileService
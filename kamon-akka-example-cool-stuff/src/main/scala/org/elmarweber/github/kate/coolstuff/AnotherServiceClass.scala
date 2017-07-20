package org.elmarweber.github.kate.coolstuff

import com.typesafe.scalalogging.StrictLogging
import org.elmarweber.github.kate.lib.kamon.InstrumentationSupport

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Random

object AnotherServiceClass extends InstrumentationSupport with StrictLogging {
  def calculateDelay()(implicit ec: ExecutionContext): Future[Long] = traceFuture("calculateDelay") {
    Future {
      val delay = 500 + Random.nextInt(1000)
      logger.info(s"Determined a delay of ${delay}")
      delay
    }
  }
}

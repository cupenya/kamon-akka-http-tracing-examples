package org.elmarweber.github.kate.coolstuff

import com.typesafe.scalalogging.StrictLogging
import org.elmarweber.github.kate.lib.kamon.InstrumentationSupport

import scala.concurrent.{ExecutionContext, Future}

object CalculationClass extends InstrumentationSupport with StrictLogging {
  def complexCalc(delay: Long)(implicit ec: ExecutionContext): Future[Long] = traceFuture("complexCalc") {
    Future {
      logger.info("Doing calculation")
      Thread.sleep(delay)
      delay
    }
  }
}

package org.elmarweber.github.kate.lib.kamon

import com.typesafe.scalalogging.StrictLogging
import kamon.Kamon
import kamon.util.CallingThreadExecutionContext

import scala.concurrent.Future

/**
 * Contain support methods when using AspectJ to instrument the application for tracing with Kamon.
 * Works at the moment only with Future methods as these present boundaries that are worth tracing.
 *
 */
trait InstrumentationSupport extends StrictLogging {
  def traceFuture[T](name: String)(f: => Future[T]): Future[T] = {
    val newSpan = Kamon.tracer.buildSpan(name).start()
    val activatedSpan = Kamon.makeActive(newSpan)
    val evaluatedFuture = f.transform(
      r => { newSpan.finish(); r},
      t => { newSpan.finish(); t}
    )(CallingThreadExecutionContext)
    activatedSpan.deactivate()
    evaluatedFuture
  }
}

object InstrumentationSupport extends InstrumentationSupport


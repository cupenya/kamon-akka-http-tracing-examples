package org.elmarweber.github


import com.typesafe.scalalogging.StrictLogging

/**
 * Contain support methods when using AspectJ to instrument the application for tracing with Kamon.
 * Works at the moment only with Future methods as these present boundaries that are worth tracing.
 *
 */
trait InstrumentationSupport extends StrictLogging {

}

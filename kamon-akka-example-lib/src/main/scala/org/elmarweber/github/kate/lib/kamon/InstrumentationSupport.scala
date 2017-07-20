package org.elmarweber.github.kate.lib.kamon

import com.typesafe.scalalogging.StrictLogging
import kamon.Kamon
import kamon.trace.Tracer.SpanBuilder
import kamon.trace.{Span, SpanContext}
import kamon.util.CallingThreadExecutionContext

import scala.concurrent.Future

/**
 * Contain support methods when using AspectJ to instrument the application for tracing with Kamon.
 * Works at the moment only with Future methods as these present boundaries that are worth tracing.
 *
 */
trait InstrumentationSupport extends StrictLogging {
  private lazy val defaultPrefix = simpleClassName + "."
  private lazy val fullClassName = getClass().getName
  private lazy val cachedEnv = sys.env

  private lazy val simpleClassName = getClass().getSimpleName
  protected def prefix = defaultPrefix


  def addDefaultTags(builder: SpanBuilder) = {
    builder.withSpanTag("class", fullClassName)
    // TODO: replace with default tags
    cachedEnv.get("KUBERNETES_POD").foreach { pod =>
      builder.withSpanTag("kubernetes.pod", pod)
    }
    cachedEnv.get("KUBERNETES_NAMESPACE").foreach { namespace =>
      builder.withSpanTag("kubernetes.namespace", namespace)
    }
    cachedEnv.get("KUBERNETES_NODE").foreach { node =>
      builder.withSpanTag("kubernetes.node", node)
    }
  }

  def traceFuture[T](name: String)(f: => Future[T]): Future[T] = {
    val newSpan = addDefaultTags(Kamon.tracer.buildSpan(prefix + name)).start()
    val activatedSpan = Kamon.makeActive(newSpan)
    val evaluatedFuture = f.transform(
      r => { newSpan.finish(); r},
      t => { newSpan.finish(); t}
    )(CallingThreadExecutionContext)
    activatedSpan.deactivate()
    evaluatedFuture
  }

  def traceBlock[T](name: String, parentSpanContext: Option[SpanContext] = None)(f: => T): T = {
    val newSpan = addDefaultTags(Kamon.tracer.buildSpan(prefix + name)).asChildOf(parentSpanContext).start()
    val activatedSpan = Kamon.makeActive(newSpan)
    try {
      f
    } finally {
      activatedSpan.deactivate()
      newSpan.finish()
    }
  }
}

object InstrumentationSupport extends InstrumentationSupport


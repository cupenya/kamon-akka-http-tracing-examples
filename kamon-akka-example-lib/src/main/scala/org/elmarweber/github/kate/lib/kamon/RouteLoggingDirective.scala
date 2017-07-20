package org.elmarweber.github.kate.lib.kamon

import java.util.concurrent.atomic.AtomicLong

import akka.http.scaladsl.model.HttpHeader
import akka.http.scaladsl.server._
import akka.http.scaladsl.server.directives.BasicDirectives
import kamon.Kamon
import kamon.trace.SpanContextCodec.Format
import kamon.trace.TextMap
import kamon.util.CallingThreadExecutionContext

import scala.util.Random

trait RouteLoggingDirective extends BasicDirectives {
  
  private val randomSeed = Random.nextInt(100000)

  private val requestIdCounter = new AtomicLong(1)
  protected def additionalTraceId = "trace"

  def trace: Directive[Unit] = _trace

  private val _trace = Directive[Unit] {
    innerRoute ⇒ ctx ⇒ {
      val traceId = s"req-$randomSeed-${requestIdCounter.getAndIncrement()}-${additionalTraceId}"
      val textMap = readOnlyTextMapFromHeaders(ctx.request.headers)
      val incomingSpanContext = Kamon.extract(Format.HttpHeaders, textMap)
      val serverSpan = Kamon.buildSpan(ctx.request.uri.path.toString)
        .asChildOf(incomingSpanContext)
        .withSpanTag("myTraceId", traceId)
        .startActive()

      val innerRouteResult = innerRoute(ctx)(ctx)
      innerRouteResult.onComplete(_ => serverSpan.finish())(CallingThreadExecutionContext)
      serverSpan.deactivate()

      innerRouteResult
    }
  }

  def readOnlyTextMapFromHeaders(headers: Seq[HttpHeader]): TextMap = new TextMap {
    private val headersMap = headers.map { h => h.name -> h.value }.toMap
    override def get(key: String): Option[String] = headersMap.get(key)

    override def put(key: String, value: String): Unit = ???

    override def values: Iterator[(String, String)] = headersMap.iterator
  }
}

object RouteLoggingDirective {
  val TraceIdHeader = "X─B3─TraceId"
  val ParentSpanIdHeader = "X─B3─ParentSpanId"
}


package org.elmarweber.github

import java.io.StringWriter
import java.util
import java.util.Map
import java.util.concurrent.atomic.AtomicLong

import akka.http.scaladsl.server.directives.BasicDirectives
import akka.actor._
import akka.http.scaladsl.model.HttpHeader
import akka.http.scaladsl.server._
import akka.http.scaladsl.server.directives.BasicDirectives.{extractRequestContext, mapRouteResult}
import io.opentracing.propagation.Format.Builtin.HTTP_HEADERS
import io.opentracing.propagation.TextMap
import kamon.Kamon
import kamon.trace.Tracer
import scala.collection.JavaConverters._

import scala.util.Random

trait RouteLoggingDirective extends BasicDirectives {
  import RouteLoggingDirective._
  
  private val randomSeed = Random.nextInt(100000)

  private val requestIdCounter = new AtomicLong(1)
  protected def additionalTraceId = "trace"

  def trace: Directive0 =
    extractRequestContext.flatMap { ctx ⇒
      val traceId = s"req-$randomSeed-${requestIdCounter.getAndIncrement()}-${additionalTraceId}"
      val textMap = readOnlyTextMapFromHeaders(ctx.request.headers)
      val incomingSpanContext = Kamon.extract(HTTP_HEADERS, textMap)
      val span = Kamon.buildSpan(ctx.request.uri.path.toString).asChildOf(incomingSpanContext).withTag("myTraceId", traceId).startManual()
      val activeSpan = Kamon.makeActive(span)

      mapRouteResult { result ⇒
        span.finish()
        //val responseWithTraceHeader = response.copy(headers = RawHeader(KamonTraceRest.PublicHeaderName, traceId) :: response.headers)
        println(traceId + " done")
        result
      }
    }

  def readOnlyTextMapFromHeaders(headers: Seq[HttpHeader]): TextMap = new TextMap {
    override def put(key: String, value: String): Unit = {}

    override def iterator(): util.Iterator[Map.Entry[String, String]] =
      headers.map(h => new Map.Entry[String, String] {
        override def getKey: String = h.name()
        override def getValue: String = h.value()
        override def setValue(value: String): String = value
      }).iterator.asJava
  }
}

object RouteLoggingDirective {
  val TraceIdHeader = "X─B3─TraceId"
  val ParentSpanIdHeader = "X─B3─ParentSpanId"
}


package org.elmarweber.github

import java.util.concurrent.atomic.AtomicLong

import akka.http.scaladsl.model.HttpHeader
import akka.http.scaladsl.server._
import akka.http.scaladsl.server.directives.BasicDirectives
import kamon.Kamon
import kamon.trace.TextMap

import scala.util.Random

trait RouteLoggingDirective extends BasicDirectives {
  
  private val randomSeed = Random.nextInt(100000)

  private val requestIdCounter = new AtomicLong(1)
  protected def additionalTraceId = "trace"

  def trace: Directive0 =
    extractRequestContext.flatMap { ctx ⇒
      val traceId = s"req-$randomSeed-${requestIdCounter.getAndIncrement()}-${additionalTraceId}"
      val textMap = readOnlyTextMapFromHeaders(ctx.request.headers)
      val incomingSpanContext = Kamon.extract(kamon.trace.SpanContextCodec.Format.TextMap, textMap)
      val span = {
        val builder = Kamon.buildSpan(ctx.request.uri.path.toString)
        incomingSpanContext.foreach(builder.asChildOf)
        builder.withSpanTag("myTraceId", traceId)
        builder.start()
      }
      val activeSpan = Kamon.makeActive(span)

      mapRouteResult { result ⇒
        span.finish()
        activeSpan.deactivate()
        //val responseWithTraceHeader = response.copy(headers = RawHeader(KamonTraceRest.PublicHeaderName, traceId) :: response.headers)
        println(traceId + " done")
        result
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


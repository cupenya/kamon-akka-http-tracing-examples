package org.elmarweber.github

import java.util
import java.util.Map

import akka.http.scaladsl.model.StatusCodes.ServerError
import akka.http.scaladsl.model.{HttpHeader, StatusCodes}
import akka.http.scaladsl.server.directives.BasicDirectives
import akka.http.scaladsl.server.Directive0
import akka.http.scaladsl.server.RouteResult.{Complete, Rejected}
import io.opentracing.propagation.TextMap
import io.opentracing.propagation.Format.Builtin.HTTP_HEADERS
import kamon.Kamon

import scala.collection.JavaConverters._


trait ServiceMetricDirectives extends BasicDirectives {
  def trace(operationName: String): Directive0 = {
    extractRequest.flatMap { request =>
      val textMap = readOnlyTextMapFromHeaders(request.headers)
      val incomingSpanContext = Kamon.extract(HTTP_HEADERS, textMap)
      val span = Kamon.buildSpan(operationName).asChildOf(incomingSpanContext).startManual()

      mapRouteResult(response => {
        span.finish()
        response
      })
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
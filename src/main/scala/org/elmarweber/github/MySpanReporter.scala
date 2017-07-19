package org.elmarweber.github

import com.typesafe.config.Config
import com.typesafe.scalalogging.StrictLogging
import kamon.SpanReporter
import kamon.trace.Span
import kamon.util.HexCodec

class MySpanReporter extends SpanReporter with StrictLogging {
  override def reportSpans(spans: Seq[Span.CompletedSpan]): Unit = {
     spans.foreach { span =>
       logger.info(s"${HexCodec.toLowerHex(span.context.traceID)} ${HexCodec.toLowerHex(span.context.spanID)} ${HexCodec.toLowerHex(span.context.parentID)} ${span.tags}")
     }
  }

  override def start(): Unit = {}

  override def stop(): Unit = {}

  override def reconfigure(config: Config): Unit = {}
}

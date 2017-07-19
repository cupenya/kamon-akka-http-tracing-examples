package org.elmarweber.github

import com.typesafe.config.Config
import com.typesafe.scalalogging.StrictLogging
import kamon.SpanReporter
import kamon.trace.Span
import kamon.util.HexCodec

class MySpanReporter extends SpanReporter with StrictLogging {

  private def duration(span: Span.FinishedSpan): Double = {
    (span.endTimestampMicros - span.startTimestampMicros).toDouble / 1000 / 1000
  }

  override def reportSpans(spans: Seq[Span.FinishedSpan]): Unit = {
     spans.foreach { span =>
       logger.info(s"${(span.context.traceID.string)} ${(span.context.spanID.string)} ${(span.context.parentID.string)} ${span.operationName} ${duration(span)} ${span.tags}")
     }
  }

  override def start(): Unit = {}

  override def stop(): Unit = {}

  override def reconfigure(config: Config): Unit = {}
}

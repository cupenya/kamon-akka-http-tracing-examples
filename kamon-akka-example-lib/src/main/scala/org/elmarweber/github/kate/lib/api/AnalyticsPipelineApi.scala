package org.elmarweber.github.kate.lib.api

import akka.http.scaladsl.client.RequestBuilding
import org.elmarweber.github.kate.lib.httpclient.HttpClient
import spray.json.{DefaultJsonProtocol, JsString}
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._

import scala.concurrent.{ExecutionContext, Future}

sealed trait AnalyticsPipelineModel

object AnalyticsPipelineModel extends DefaultJsonProtocol {
  implicit val AnalyticsEventFormat = jsonFormat3(AnalyticsEvent)
}


case class AnalyticsEvent(userId: String, country: String, event: String) extends AnalyticsPipelineModel

trait AnalyticsPipelineApi {
  def logEvent(event: AnalyticsEvent): Future[Unit]
}

class AnalyticsPipelineApiClient(httpClient: HttpClient)(implicit ec: ExecutionContext) extends AnalyticsPipelineApi with DefaultJsonProtocol {
  override def logEvent(event: AnalyticsEvent): Future[Unit] = {
    val req = RequestBuilding.Post("/api/logEvent", event)
    httpClient.doTypedRest[Unit](req)
  }
}
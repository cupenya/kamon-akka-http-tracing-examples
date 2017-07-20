package org.elmarweber.github.kate.lib.api

import akka.http.scaladsl.client.RequestBuilding
import org.elmarweber.github.kate.lib.httpclient.HttpClient
import spray.json.{DefaultJsonProtocol, JsString}
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._

import scala.concurrent.{ExecutionContext, Future}

sealed trait CoolStuffModel

object CoolStuffModel extends DefaultJsonProtocol {
  implicit val CoolStuffResponseFormat = jsonFormat1(CoolStuffResponse)
}


case class CoolStuffResponse(message: String) extends CoolStuffModel

trait CoolStuffApi {
  def doCoolStuff(): Future[CoolStuffResponse]
}

class CoolStuffApiClient(httpClient: HttpClient)(implicit ec: ExecutionContext) extends CoolStuffApi with DefaultJsonProtocol {
  override def doCoolStuff(): Future[CoolStuffResponse] = {
    val req = RequestBuilding.Get("/api/cool-stuff/do")
    httpClient.doTypedRest[CoolStuffResponse](req)
  }
}
package org.elmarweber.github.kate.lib.api

import akka.http.scaladsl.client.RequestBuilding
import org.elmarweber.github.kate.lib.httpclient.HttpClient
import spray.json.DefaultJsonProtocol
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._

import scala.concurrent.{ExecutionContext, Future}

sealed trait AuthModel

object AuthModel extends DefaultJsonProtocol {
  implicit val AuthRequestFormat = jsonFormat1(AuthRequest)
  implicit val AuthResponseFormat = jsonFormat2(AuthResponse)
}

case class AuthRequest(apiKey: String) extends AuthModel
case class AuthResponse(apiKey: String, userId: String) extends AuthModel


trait AuthApi {
  def doAuth(request: AuthRequest): Future[AuthResponse]
}

class AuthHttpApiClient(httpClient: HttpClient)(implicit ec: ExecutionContext) extends AuthApi {
  override def doAuth(request: AuthRequest): Future[AuthResponse] = {
    val req = RequestBuilding.Post("/api/auth", request)
    httpClient.doTypedRest[AuthResponse](req)
  }
}
package org.elmarweber.github.kate.lib.api

import akka.http.scaladsl.client.RequestBuilding
import org.elmarweber.github.kate.lib.httpclient.HttpClient
import spray.json.DefaultJsonProtocol

import scala.concurrent.{ExecutionContext, Future}

sealed trait ProfileModel

object ProfileModel extends DefaultJsonProtocol {
  implicit val UserProfileFormat = jsonFormat3(UserProfile)
}

case class UserProfile(id: String, name: String, country: String) extends ProfileModel


trait ProfileApi {
  def get(id: String): Future[UserProfile]
}

class ProfileHttpApiClient(httpClient: HttpClient)(implicit ec: ExecutionContext) extends ProfileApi {
  override def get(id: String): Future[UserProfile] = {
    val req = RequestBuilding.Get(s"/api/profiles/${id}")
    httpClient.doTypedRest[UserProfile](req)
  }
}
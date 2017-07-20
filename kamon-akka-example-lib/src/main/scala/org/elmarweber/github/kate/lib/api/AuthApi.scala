package org.elmarweber.github.kate.lib.api

import spray.json.DefaultJsonProtocol

sealed trait AuthApi

object AuthApi extends DefaultJsonProtocol {
  implicit val AuthRequestFormat = jsonFormat1(AuthRequest)
  implicit val AuthResponseFormat = jsonFormat2(AuthResponse)
}

case class AuthRequest(apiKey: String) extends AuthApi
case class AuthResponse(apiKey: String, verifiedToken: String) extends AuthApi

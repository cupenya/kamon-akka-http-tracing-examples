package org.elmarweber.github

import akka.http.scaladsl.client.RequestBuilding
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.Uri.Query
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives
import kamon.Kamon
import kamon.util.CallingThreadExecutionContext
import org.elmarweber.github.kate.lib.httpclient.HttpClient
import org.elmarweber.github.kate.lib.kamon.RouteLoggingDirective

import scala.concurrent.{ExecutionContext, Future}


trait ServiceRoute extends Directives with RouteLoggingDirective {
  implicit def ec: ExecutionContext

  implicit def api: EchoSubServiceApi

  val serviceRoute = pathPrefix("api") {
    trace {
      pathPrefix("echo") {
        pathEndOrSingleSlash {
          get {
            parameter("msg".as[String].?) { msg =>
              complete {
                EchoResponse(msg.getOrElse("OK"))
              }
            }
          }
        }
      } ~
          pathPrefix("echo-via-sub") {
            pathEndOrSingleSlash {
              get {
                parameter("msg".as[String].?) { msg =>
                  complete {
                    EchoService.doEchoSub(msg)
                  }
                }
              }
            }
          } ~
          pathPrefix("echo-sub") {
            pathEndOrSingleSlash {
              get {
                parameter("msg".as[String].?) { msg =>
                  complete {
                    EchoResponse("sub: " + msg.getOrElse("OK"))
                  }
                }
              }
            }
          }
    }
  }
}

trait EchoSubServiceApi {
  def echoSub(msg: Option[String]): Future[EchoResponse]
}

class EchoSubServiceHttpClient(client: HttpClient) extends EchoSubServiceApi {
  override def echoSub(msg: Option[String]): Future[EchoResponse] = {
    val req = RequestBuilding.Get(Uri("/api/echo-sub").withQuery(Query("msg" -> msg.getOrElse(""))))
    client.doTypedRest[EchoResponse](req)
  }
}


trait EchoService {

  private def traceFuture[T](name: String)(f: => Future[T]): Future[T] = {
    val newSpan = Kamon.tracer.buildSpan(name).start()
    val activatedSpan = Kamon.makeActive(newSpan)
    val evaluatedFuture = f.transform(
      r => { newSpan.finish(); r},
      t => { newSpan.finish(); t}
    )(CallingThreadExecutionContext)
    activatedSpan.deactivate()
    evaluatedFuture
  }

  def doEchoSleepy(msg: Option[String])(implicit ec: ExecutionContext): Future[EchoResponse] = {
    Future {
      EchoResponse(msg.getOrElse("OK"))
    }
  }

  // does not work, trace is not collected
  def doExpensiveOperation()(implicit ec: ExecutionContext): Future[Int] = traceFuture("doExpensiveOperation") {
    Future {
      Thread.sleep(500)
      1
    }
  }


  def doEchoSub(msg: Option[String])(implicit ec: ExecutionContext, api: EchoSubServiceApi): Future[EchoResponse] = {
    doExpensiveOperation.flatMap { _ =>
      api.echoSub(msg).map(subMsg => subMsg.copy(echo = subMsg.echo + " (via)"))
    }
  }
}

object EchoService extends EchoService
package org.elmarweber.github.kate.analyticspipeline

import akka.actor.{Actor, ActorRef, ActorSystem, Stash}
import akka.stream.scaladsl.{Sink, Source}
import akka.stream.{ActorMaterializer, OverflowStrategy}
import com.typesafe.scalalogging.StrictLogging
import org.elmarweber.github.kate.analyticspipeline.AnalyticsStreamActor.StreamInitActorRef
import org.elmarweber.github.kate.lib.api.AnalyticsEvent

import scala.concurrent.Future
import scala.util.{Failure, Random, Success}

class AnalyticsStreamActor extends Actor with StrictLogging with Stash {
  private implicit val sys = ActorSystem("analytics-stream-system")
  private implicit val ec = sys.dispatcher
  private implicit val materializer = ActorMaterializer()

  private var streamSourceActorRef: Option[ActorRef] = None

  override def preStart(): Unit = {
    val stream = Source.actorRef[AnalyticsEvent](4096, overflowStrategy = OverflowStrategy.dropBuffer)
      .mapMaterializedValue { actorRef =>
        self ! StreamInitActorRef(actorRef)
        actorRef
      }
      .mapAsyncUnordered(4) { event =>
        Future {
          Thread.sleep(1000)
          (event, Random.nextInt(100000))
        }
      }.map { case (event, users) =>
         logger.info(s"Processed ${event.userId} ${users}")
         Thread.sleep(100)
      }
      .runWith(Sink.ignore)
    stream.onComplete {
      case Success(done) =>
        logger.info("Metric refresh stream terminated normally")
      case Failure(ex) =>
        logger.error(s"Metric refresh stream crashed: ${ex.getMessage}", ex)
    }
  }

  override def receive = receiveInit


  def receiveInit: Receive = {
    case StreamInitActorRef(actorRef) =>
      streamSourceActorRef = Some(actorRef)
      unstashAll()
      context.become(receiveReady)
    case msg => stash()
  }

  def receiveReady: Receive = {
    case e: AnalyticsEvent =>
      streamSourceActorRef.getOrElse(throw new IllegalStateException("Error during init, no actor ref")) ! e
  }



  override def postStop(): Unit = {
    materializer.shutdown()
    sys.terminate()
  }
}

object AnalyticsStreamActor {
  sealed trait AnalyticsStreamActorProtocol

  private case class StreamInitActorRef(actorRef: ActorRef) extends AnalyticsStreamActorProtocol
}

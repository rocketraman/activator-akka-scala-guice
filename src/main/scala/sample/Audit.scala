package sample

import akka.actor.{Actor, ActorRef}
import akkaguice.NamedActor
import com.google.inject.{BindingAnnotation, Inject}

import scala.annotation.StaticAnnotation

object AuditCompanion extends NamedActor {
  override def name: String = "AuditCompanion"
}

class AuditCompanion @Inject() (@Audit auditBus: ActorRef) extends Actor {

  val created = System.currentTimeMillis

  def receive = {
    case msg => auditBus forward AuditBus.AuditEvent(created, msg)
  }
}

object AuditBus extends NamedActor {
  override def name: String = "AuditBus"

  case class AuditEvent(auditCompanionCreated: Long, msg: Any)
}

@BindingAnnotation
class Audit extends StaticAnnotation

class AuditBus extends Actor {

  def receive = {
    case AuditBus.AuditEvent(companionCreated, msg) =>
      println(s"[AuditBus:${self.hashCode()}] Message '$msg' received from '$sender'. AuditCompanion created at '$companionCreated'.")
  }
}

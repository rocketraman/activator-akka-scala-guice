package sample

import akka.actor.{ActorRef, Actor}
import akkaguice.NamedActor
import com.google.inject.{Inject, BindingAnnotation}

import scala.annotation.StaticAnnotation

object AuditCompanion extends NamedActor {
  override def name: String = "AuditCompanion"

  case class AuditEvent(auditeeCreated: Long, msg: Any)
}

class AuditCompanion @Inject() (@Audit auditBus: ActorRef) extends Actor {

  import AuditCompanion._

  val created = System.currentTimeMillis

  def receive = {
    case msg => auditBus forward AuditEvent(created, msg)
  }
}

object AuditBus extends NamedActor {
  override def name: String = "AuditBus"
}

@BindingAnnotation
class Audit extends StaticAnnotation

class AuditBus extends Actor {

  import AuditCompanion._

  def receive = {
    case AuditEvent(created, msg) => println(s"[AuditBus:${self.hashCode()}] Message '$msg' received from '$sender'. Auditee created at '$created'.")
  }
}

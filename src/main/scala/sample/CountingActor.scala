package sample

import akka.actor.Actor
import akkaguice.NamedActor
import javax.inject.Inject

object CountingActor extends NamedActor {

  // this (and the NamedActor trait) is not required here -- it is simply a convenience so that the name
  // can be defined and referenced from one place
  override def name = "CountingActor"

  case object Count
  case object Get
}

/**
 * An actor that can count using an injected CountingService.
 *
 * @note The scope will be left at the default value in the Guice module, which is equivalent to the Spring
 *   "prototype" scope i.e. a new Actor will be created whenever a request is made for this dependency
 *   from Guice.
 */
class CountingActor @Inject() (countingService: CountingService) extends Actor {

  import CountingActor._

  private var count: Int = 0

  def receive = {
    case Count =>
      count = countingService.increment(count)
    case Get => sender ! count
  }

}

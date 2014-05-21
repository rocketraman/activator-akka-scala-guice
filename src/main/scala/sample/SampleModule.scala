package sample

import akka.actor.Actor
import net.codingwell.scalaguice.ScalaModule
import com.google.inject.AbstractModule
import com.google.inject.name.Names

/**
 * A Guice module for the counting actor and service.
 */
class SampleModule extends AbstractModule with ScalaModule {
  override def configure() {
    // note that we DO NOT define the actor in scope singleton, and we MUST name it
    bind[Actor].annotatedWith(Names.named(CountingActor.name)).to[CountingActor]
    //bind[CountingService]
  }
}

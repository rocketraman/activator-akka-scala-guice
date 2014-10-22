package sample

import javax.inject.Inject

import akka.actor.{Actor, ActorRef, ActorSystem}
import akkaguice.GuiceAkkaActorRefProvider
import com.google.inject.name.{Named, Names}
import com.google.inject.{AbstractModule, Provides, Singleton}
import net.codingwell.scalaguice.ScalaModule

/**
 * A Guice module for the audit actors.
 *
 * This module provides top level actors for wiring into other actor constructors. Top level actors should be
 * used sparingly and only to wire-up few top-level components.
 */
class AuditModule extends AbstractModule with ScalaModule with GuiceAkkaActorRefProvider {
  override def configure() {
    bind[Actor].annotatedWith(Names.named(AuditCompanion.name)).to[AuditCompanion]
    bind[Actor].annotatedWith(Names.named(AuditBus.name)).to[AuditBus]
  }

  @Provides
  @Named(AuditCompanion.name)
  def provideAuditCompanionRef(@Inject() system: ActorSystem): ActorRef = provideActorRef(system, AuditCompanion.name)

  @Provides
  @Singleton
  @Audit
  def provideAuditBusRef(@Inject() system: ActorSystem): ActorRef = provideActorRef(system, AuditBus.name)

}

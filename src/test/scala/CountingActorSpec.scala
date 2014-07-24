import javax.inject.Inject

import akka.actor.{ActorRef, ActorSystem}
import akka.testkit.{ImplicitSender, TestKit, TestProbe}
import akkaguice.{AkkaModule, GuiceAkkaExtension}
import com.google.inject.name.Named
import com.google.inject.{AbstractModule, Guice, Provides}
import config.ConfigModule
import net.codingwell.scalaguice.InjectorExtensions._
import net.codingwell.scalaguice.ScalaModule
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}
import sample.CountingActor.Count
import sample.{CountingActor, SampleModule}

class CountingActorSpec(_system: ActorSystem) extends TestKit(_system) with ImplicitSender with WordSpecLike
  with Matchers with BeforeAndAfterAll {

  def this() = this(ActorSystem("CountingActorSpec"))

  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }

  "a CountingActor" must {
    "send messages to audit companion" in {

      val auditCompanionProbe: TestProbe = new TestProbe(_system)

      val injector = Guice.createInjector(
        new ConfigModule(),
        new AkkaModule(),
        new SampleModule(),
        new AbstractModule with ScalaModule {
          override def configure() { }

          @Provides
          @Named("AuditCompanion")
          def provideEchoActorRef(@Inject() system: ActorSystem): ActorRef = auditCompanionProbe.ref
        }
      )

      val system = injector.instance[ActorSystem]
      val counter = system.actorOf(GuiceAkkaExtension(system).props(CountingActor.name))

      counter ! Count
      auditCompanionProbe.expectMsgClass(classOf[String])
    }
  }

}

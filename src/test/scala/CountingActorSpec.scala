import javax.inject.{Inject, Singleton}

import akka.actor.{ActorRef, ActorSystem}
import akka.pattern.ask
import akka.testkit.{ImplicitSender, TestKit, TestProbe}
import akkaguice.GuiceAkkaExtension
import com.google.inject._
import com.google.inject.name.Named
import config.ConfigModule
import net.codingwell.scalaguice.InjectorExtensions._
import net.codingwell.scalaguice.ScalaModule
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}
import sample.CountingActor.{Count, Get}
import sample._

import scala.collection.JavaConverters._
import scala.concurrent.Await
import scala.concurrent.duration._

class CountingActorSpec(_system: ActorSystem) extends TestKit(_system) with ImplicitSender with WordSpecLike
  with Matchers with BeforeAndAfterAll {

  def this() = this(ActorSystem("CountingActorSpec"))

  override def afterAll() {
    TestKit.shutdownActorSystem(system)
  }

  trait AkkaGuiceInjector {
    var injector: Injector = _

    def initInjector(testModules: Module*) = {
      val modules = List(
        new ConfigModule(),
        new AbstractModule with ScalaModule {
          override def configure() { }
          @Provides
          def provideSystem() = _system
        },
        new SampleModule()
      ) ++ testModules

      injector = Guice.createInjector(modules.asJava)
      GuiceAkkaExtension(system).initialize(injector)
    }
  }

  trait Counter extends AkkaGuiceInjector {
    // this val must be lazy so that counter is created after the injector is initialized via initInjector
    lazy val counter = system.actorOf(GuiceAkkaExtension(system).props(CountingActor.name))
  }

  "a Guice-managed count actor" must {
    "sends the correct count to its counting service" in new Counter {
      initInjector(
        new AuditModule(),
        new AbstractModule with ScalaModule {
          override def configure() {
            bind[CountingService].to[TestCountingService].in[Singleton]
          }
        }
      )

      // tell it to count three times
      counter ! Count
      counter ! Count
      counter ! Count

      // check that it has counted correctly
      val duration = 3.seconds
      val result = counter.ask(Get)(duration).mapTo[Int]
      Await.result(result, duration) should be (3)

      // check that it called the TestCountingService the right number of times
      val testService = injector.instance[CountingService].asInstanceOf[TestCountingService]
      testService.getNumberOfCalls should be(3)
    }

    "sends messages to its audit companion" in new Counter {
      val auditCompanionProbe: TestProbe = new TestProbe(_system)
      initInjector(new AbstractModule with ScalaModule {
        override def configure() {}
        @Provides
        @Named(AuditCompanion.name)
        def provideActorRef(@Inject() system: ActorSystem): ActorRef = auditCompanionProbe.ref
      })

      counter ! Count
      auditCompanionProbe.expectMsgClass(classOf[String])
    }
  }

}

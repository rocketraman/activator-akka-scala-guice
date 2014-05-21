import akka.actor.ActorSystem
import akka.pattern.ask
import akkaguice.{GuiceAkkaExtension, AkkaModule}
import com.google.inject.{AbstractModule, Guice}
import config.ConfigModule
import javax.inject.Singleton
import net.codingwell.scalaguice.InjectorExtensions._
import net.codingwell.scalaguice.ScalaModule
import org.scalatest.{Matchers, FlatSpec}
import sample.{CountingService, CountingActor, SampleModule}
import sample.CountingActor.{Get, Count}
import scala.concurrent.duration._
import scala.concurrent.Await

class GuiceTest extends FlatSpec with Matchers {

  trait AkkaGuiceSystem {
    val injector = Guice.createInjector(
      new ConfigModule(),
      new AkkaModule(),
      new SampleModule()
    )

    val system = injector.instance[ActorSystem]
  }

  trait Counter extends AkkaGuiceSystem {
    val counter = system.actorOf(GuiceAkkaExtension(system).props(CountingActor.name))
  }

  "A Guice-managed count actor" should "product the correct count" in new Counter {
    // tell it to count three times
    counter ! Count
    counter ! Count
    counter ! Count

    // check that it has counted correctly
    val duration = 3.seconds
    val result = counter.ask(Get)(duration).mapTo[Int]
    Await.result(result, duration) should be (3)

    // shut down the actor system
    system.shutdown()
    system.awaitTermination()
  }
}

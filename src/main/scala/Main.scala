import akka.actor.ActorSystem
import akka.pattern.ask
import akkaguice.{GuiceAkkaExtension, AkkaModule}
import com.google.inject.Guice
import config.ConfigModule
import net.codingwell.scalaguice.InjectorExtensions._
import sample.CountingActor.{Get, Count}
import sample.{CountingActor, SampleModule}
import scala.concurrent.Await
import scala.concurrent.duration._

/**
 * A main class to start up the application.
 */
object Main extends App {

  val injector = Guice.createInjector(
    new ConfigModule(),
    new AkkaModule(),
    new SampleModule()
  )

  val system = injector.instance[ActorSystem]

  // this could be called inside a supervisor actor to create a supervisor hierarchy,
  // using context.actorOf(GuiceAkkaExtension(context.system)...
  val counter = system.actorOf(GuiceAkkaExtension(system).props(CountingActor.name))

  // tell it to count three times
  counter ! Count
  counter ! Count
  counter ! Count

  // print the result
  val duration = 3.seconds
  val result = counter.ask(Get)(duration).mapTo[Int]
  try {
    println(s"Got back ${Await.result(result, duration)}")
  }
  catch {
    case e: Exception =>
      Console.err.println(s"Failed getting result: ${e.getMessage}")
      throw e
  }
  finally {
    system.shutdown()
    system.awaitTermination()
  }

}

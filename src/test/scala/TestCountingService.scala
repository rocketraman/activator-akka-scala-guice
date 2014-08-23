import com.typesafe.config.Config
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Inject
import sample.CountingService

class TestCountingService @Inject() (config: Config) extends CountingService(config) {
  private val called = new AtomicInteger(0)

  override def increment(count: Int) = {
    called.incrementAndGet()
    super.increment(count)
  }

  def getNumberOfCalls: Int = called.get()
}

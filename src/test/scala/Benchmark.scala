import scala.concurrent.Await
import scala.concurrent.duration.Duration

object BenchmarkTests extends App {
  // Test 1: Single URL Scrape Time
  val singleUrlInput = "input_single_url.txt"
  val singleUrlSystem = ActorSystem(RootActor(singleUrlInput), "SingleUrlScraper")
  singleUrlSystem ! ""
  Thread.sleep(1000) // Give some time for the system to start
  Await.result(singleUrlSystem.whenTerminated, Duration.Inf)

  // Test 2: Multiple URLs Sequential Scrape Time
  val sequentialInput = "input_sequential_urls.txt"
  val sequentialSystem = ActorSystem(RootActor(sequentialInput), "SequentialScraper")
  sequentialSystem ! ""
  Thread.sleep(1000)
  Await.result(sequentialSystem.whenTerminated, Duration.Inf)

  // Test 3: Multiple URLs Parallel Scrape Time
  val parallelInput = "input_parallel_urls.txt"
  val parallelSystem = ActorSystem(RootActor(parallelInput), "ParallelScraper")
  parallelSystem ! ""
  Thread.sleep(1000)
  Await.result(parallelSystem.whenTerminated, Duration.Inf)

  // Test 4: Scrape with Timeout Handling
  val timeoutInput = "input_timeout_urls.txt"
  val timeoutSystem = ActorSystem(RootActor(timeoutInput), "TimeoutScraper")
  timeoutSystem ! ""
  Thread.sleep(1000)
  Await.result(timeoutSystem.whenTerminated, Duration.Inf)

  // Test 5: Scrape Error Handling
  val errorInput = "input_error_urls.txt"
  val errorSystem = ActorSystem(RootActor(errorInput), "ErrorHandlingScraper")
  errorSystem ! ""
  Thread.sleep(1000)
  Await.result(errorSystem.whenTerminated, Duration.Inf)
}

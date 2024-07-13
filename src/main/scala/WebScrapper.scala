import akka.actor.{Actor, ActorSystem, Props}
import akka.pattern.ask
import akka.routing.{ActorRefRoutee, RoundRobinRoutingLogic, Router}
import akka.util.Timeout
import org.jsoup.Jsoup

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global

object WebScraper {

  // Actor to fetch and process URLs
  class ScraperActor extends Actor {
    def receive: Receive = {
      case url: String =>
        try {
          val doc = Jsoup.connect(url).get()
          val title = doc.title()
          val author = doc.select("meta[name=author]").attr("content")
          val date = doc.select("meta[name=publication_date]").attr("content")
          sender() ! Some((url, title, author, date))
        } catch {
          case e: Exception =>
            println(s"Error fetching $url: ${e.getMessage}")
            sender() ! None
        }
    }
  }

  def main(args: Array[String]): Unit = {
    val system = ActorSystem("WebScraperSystem")

    val router = {
      val routees = Vector.fill(5) {
        val r = system.actorOf(Props[ScraperActor])
        ActorRefRoutee(r)
      }
      Router(RoundRobinRoutingLogic(), routees)
    }

    // Function to scrape URLs in parallel
    def scrape(urls: List[String]): List[Future[Option[(String, String, String, String)]]] = {
      implicit val timeout: Timeout = Timeout(20.seconds) // Increased timeout due to potential network delays

      val futures = urls.map(url => {
        val randomRoutee = router.routees(util.Random.nextInt(router.routees.size)).asInstanceOf[ActorRefRoutee].ref
        (randomRoutee ? url).mapTo[Option[(String, String, String, String)]]
      })
      futures
    }

    // Example usage
    if (args.length < 2) {
      println("Usage: sbt \"run -link <file-path>\"")
      system.terminate()
    } else {
      val filePath = args(1)
      val urls = scala.io.Source.fromFile(filePath).getLines().toList
      val futures = scrape(urls)
      val results = Await.result(Future.sequence(futures), 30.seconds).flatten

      // Process and print results
      DataProcessor.process(results)

      // Terminate the actor system
      system.terminate()
    }
  }
}

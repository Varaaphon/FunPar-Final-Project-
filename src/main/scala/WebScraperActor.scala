import akka.actor.{Actor, ActorSystem, Props}
import scala.concurrent.{Future, ExecutionContext}
import ExecutionContext.Implicits.global

case class ScrapeUrl(url: String)

class WebScraperActor extends Actor {
  def receive: Receive = {
    case ScrapeUrl(url) =>
      val originalSender = sender()
      Future {
        val articles = WebScraper.fetchArticles(url)
        originalSender ! articles
      }
  }
}

object WebScraperActor {
  def props = Props[WebScraperActor]
}

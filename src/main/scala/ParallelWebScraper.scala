import akka.actor.typed.{ActorRef, ActorSystem, Behavior}
import akka.actor.typed.scaladsl.Behaviors
import akka.util.Timeout
import akka.actor.typed.scaladsl.AskPattern._
import play.api.libs.json.{JsValue, Json, Writes}

import scala.concurrent.Future
import scala.concurrent.duration._
import org.jsoup.Jsoup
import scala.concurrent.ExecutionContext
import scala.jdk.CollectionConverters._
import java.io.File
import scala.util.{Failure, Success}

object ParallelWebScraper extends App {

  sealed trait ScraperMessage
  final case class Scrape(url: String, replyTo: ActorRef[ScrapedData]) extends ScraperMessage
  final case class ScrapedData(url: String, title: Option[String], authors: Option[List[String]], abstractText: Option[String], publishedDate: Option[String])

  implicit val scrapedDataWrites: Writes[ScrapedData] = new Writes[ScrapedData] {
    def writes(data: ScrapedData): JsValue = Json.obj(
      "URL" -> Json.toJson(data.url),
      "Title" -> Json.toJson(data.title.getOrElse("")),
      "Authors" -> Json.toJson(data.authors.getOrElse(List.empty[String])),
      "Abstract" -> Json.toJson(data.abstractText.getOrElse("")),
      "Published Date" -> Json.toJson(data.publishedDate.getOrElse(""))
    )
  }

  object ScraperActor {
    def apply(): Behavior[Scrape] = Behaviors.receiveMessage { message =>
      val scrapedData = scrapeData(message.url)
      message.replyTo ! scrapedData
      Behaviors.same
    }

    def scrapeData(url: String): ScrapedData = {
      try {
        val document = Jsoup.connect(url).get()

        val title = Option(document.title()).filter(_.nonEmpty)
        val authors = Option(document.select("meta[name=citation_author]").asScala.map(_.attr("content")).toList).filter(_.nonEmpty)
        val abstractText = Option(document.select("meta[name=citation_abstract]").attr("content")).filter(_.nonEmpty)
        val publishedDate = Option(document.select("meta[name=citation_publication_date]").attr("content")).filter(_.nonEmpty)

        ScrapedData(url, title, authors, abstractText, publishedDate)
      } catch {
        case e: Exception =>
          ScrapedData(url, None, None, None, None)
      }
    }
  }

  object RootActor {
    def apply(inputFile: String): Behavior[String] = Behaviors.setup { context =>
      val scraper = context.spawn(ScraperActor(), "scraper")

      implicit val timeout: Timeout = Timeout(5.seconds)
      implicit val scheduler = context.system.scheduler
      implicit val ec: ExecutionContext = context.executionContext

      val urls = scala.io.Source.fromFile(inputFile).getLines().toList

      def scrapeUrl(url: String): Future[ScrapedData] = {
        val futureResult = scraper.ask[ScrapedData](ref => Scrape(url, ref))
        futureResult.recover {
          case _: java.util.concurrent.TimeoutException =>
            ScrapedData(url, None, None, None, None)
          case _: Exception =>
            ScrapedData(url, None, None, None, None)
        }
      }

      val allResponses: Future[List[ScrapedData]] = Future.sequence(
        urls.map(url => scrapeUrl(url))
      )

      allResponses.onComplete { result =>
        val outputFile = new File("output.txt")
        val writer = new java.io.PrintWriter(outputFile)
        result match {
          case Success(dataList) =>
            dataList.foreach { data =>
              writer.println(Json.toJson(data).toString())
              writer.println() // Print an empty line for separation
            }
          case Failure(exception) =>
            println(s"Failed to fetch all URLs: ${exception.getMessage}")
        }
        writer.close()
        context.log.info("All scraping tasks completed. Results written to output.txt.")
        context.system.terminate()
      }

      Behaviors.receiveMessage { _ =>
        Behaviors.same
      }
    }
  }

  val inputFile = args.headOption.getOrElse("input.txt")
  val system: ActorSystem[String] = ActorSystem(RootActor(inputFile), "ScraperSystem")
  system ! ""
}

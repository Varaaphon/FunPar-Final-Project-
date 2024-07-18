import org.jsoup.Jsoup
import java.io.File
import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import play.api.libs.json.{Json, Writes}

object ParallelWebScraper extends App {

  final case class ScrapedData(url: String, title: Option[String], authors: Option[List[String]], abstractText: Option[String], publishedDate: Option[String])

  implicit val scrapedDataWrites: Writes[ScrapedData] = Json.writes[ScrapedData]

  def scrapeUrl(url: String): Future[ScrapedData] = Future {
    try {
      val document = Jsoup.connect(url).get()
      val title = Option(document.title()).filter(_.nonEmpty)
      val authors = Option(document.select("meta[name=citation_author]").attr("content")).filter(_.nonEmpty).map(_.split(",").toList)
      val abstractText = Option(document.select("meta[name=citation_abstract]").attr("content")).filter(_.nonEmpty)
      val publishedDate = Option(document.select("meta[name=citation_publication_date]").attr("content")).filter(_.nonEmpty)
      ScrapedData(url, title, authors, abstractText, publishedDate)
    } catch {
      case e: Exception =>
        ScrapedData(url, None, None, None, None)
    }
  }

  def scrapeParallel(inputFile: String): Unit = {
    val urls = scala.io.Source.fromFile(inputFile).getLines().toList
    val startTime = System.nanoTime()

    val scrapedFutures = urls.map { url =>
      scrapeUrl(url)
    }

    val aggregatedResults = Future.sequence(scrapedFutures)
    val results = Await.result(aggregatedResults, 10.seconds)

    val outputFile = new File("output.txt")
    val writer = new java.io.PrintWriter(outputFile)
    results.foreach { data =>
      writer.println(Json.toJson(data).toString())
      writer.println()
    }
    writer.close()

    val endTime = System.nanoTime()
    val duration = (endTime - startTime) / 1e9d
    println(s"All scraping tasks completed in $duration seconds. Results written to output.txt.")
  }

  val inputFile = args.headOption.getOrElse("input.txt")
  scrapeParallel(inputFile)
}



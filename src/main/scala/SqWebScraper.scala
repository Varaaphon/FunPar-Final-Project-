import org.jsoup.Jsoup
import java.io.File
import scala.jdk.CollectionConverters._
import play.api.libs.json.{Json, Writes}

object SequentialWebScraper extends App {

  // Define a data structure to hold scraped information
  final case class ScrapedData(url: String, title: Option[String], authors: Option[List[String]], abstractText: Option[String], publishedDate: Option[String])

  // JSON serialization for ScrapedData
  implicit val scrapedDataWrites: Writes[ScrapedData] = new Writes[ScrapedData] {
    def writes(data: ScrapedData): play.api.libs.json.JsValue = Json.obj(
      "URL" -> Json.toJson(data.url),
      "Title" -> Json.toJson(data.title.getOrElse("")),
      "Authors" -> Json.toJson(data.authors.getOrElse(List.empty[String])),
      "Abstract" -> Json.toJson(data.abstractText.getOrElse("")),
      "Published Date" -> Json.toJson(data.publishedDate.getOrElse(""))
    )
  }

  // Function to scrape data from a single URL
  def scrapeUrl(url: String): ScrapedData = {
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

  // Function to read URLs from input file and scrape sequentially
  def scrapeSequentially(inputFile: String): Unit = {
    val urls = scala.io.Source.fromFile(inputFile).getLines().toList

    val scrapedResults = urls.map { url =>
      scrapeUrl(url)
    }

    // Write results to output.txt
    val outputFile = new File("output.txt")
    val writer = new java.io.PrintWriter(outputFile)

    scrapedResults.foreach { data =>
      writer.println(Json.toJson(data).toString())
      writer.println() // Print an empty line for separation
    }

    writer.close()
    println("All scraping tasks completed. Results written to output.txt.")
  }

  // Entry point
  val inputFile = args.headOption.getOrElse("input.txt")
  scrapeSequentially(inputFile)
}


import org.jsoup.Jsoup
import org.jsoup.select.Elements
import java.io.{File, PrintWriter}
import scala.io.Source
import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.util.{Failure, Success, Try}
import play.api.libs.json.{Json, Writes}
import java.lang.management.{ManagementFactory, MemoryMXBean, MemoryUsage}
import scala.jdk.CollectionConverters._

final case class ScrapedData(
  url: String,
  title: Option[String],
  authors: Option[List[String]],
  abstractText: Option[String],
  publishedDate: Option[String],
  error: Option[String] = None
)

object Benchmark extends App {

  implicit val scrapedDataWrites: Writes[ScrapedData] = Json.writes[ScrapedData]

  // Function to scrape a URL sequentially
  def scrapeUrlSequential(url: String): ScrapedData = {
    try {
      val document = Jsoup.connect(url).get()
      val title = Option(document.title()).filter(_.nonEmpty)
      val authors = Option(document.select("meta[name=citation_author]").asScala.map(_.attr("content")).toList).filter(_.nonEmpty)
      val abstractText = Option(document.select("meta[name=citation_abstract]").attr("content")).filter(_.nonEmpty)
      val publishedDate = Option(document.select("meta[name=citation_publication_date]").attr("content")).filter(_.nonEmpty)
      ScrapedData(url, title, authors, abstractText, publishedDate)
    } catch {
      case e: Exception =>
        ScrapedData(url, None, None, None, None, Some(e.getMessage))
    }
  }

  // Function to scrape a URL in parallel
  def scrapeUrlParallel(url: String): Future[ScrapedData] = Future {
    try {
      val doc = Jsoup.connect(url).userAgent("Mozilla/5.0").timeout(10000).get()
      val title = Option(doc.title()).filter(_.nonEmpty)
      val authors = Option(doc.select("meta[name=citation_author]").asScala.map(_.attr("content")).toList).filter(_.nonEmpty)
      val abstractText = Option(doc.select("meta[name=citation_abstract]").attr("content")).filter(_.nonEmpty)
      val publishedDate = Option(doc.select("meta[name=citation_publication_date]").attr("content")).filter(_.nonEmpty)
      ScrapedData(url, title, authors, abstractText, publishedDate, None)
    } catch {
      case e: Exception => ScrapedData(url, None, None, None, None, Some(e.getMessage))
    }
  }

  // Benchmark function
  def benchmark(methodName: String, urls: List[String], scrapeFunction: String => Any): Unit = {
    val memoryBean: MemoryMXBean = ManagementFactory.getMemoryMXBean

    println(s"Benchmarking $methodName...")

    val startMemory: MemoryUsage = memoryBean.getHeapMemoryUsage
    val startTime = System.nanoTime()

    methodName match {
      case "Sequential" =>
        urls.foreach(url => scrapeFunction(url.asInstanceOf[String]))
      case "Parallel" =>
        val scrapedFutures = urls.map(url => scrapeFunction(url.asInstanceOf[String]).asInstanceOf[Future[ScrapedData]])
        val aggregatedResults = Future.sequence(scrapedFutures)
        Await.result(aggregatedResults, 20.seconds)
    }

    val endTime = System.nanoTime()
    val endMemory: MemoryUsage = memoryBean.getHeapMemoryUsage

    val duration = (endTime - startTime) / 1e9d
    val memoryUsed = endMemory.getUsed - startMemory.getUsed

    println(s"$methodName completed in $duration seconds.")
    println(s"Memory used: ${memoryUsed / (1024 * 1024)} MB")
  }

  // Load URLs from input file
  val inputFile = "input2.txt"
  val urls = Source.fromFile(inputFile).getLines().toList

  // Benchmark sequential method
  benchmark("Sequential", urls, (url: String) => scrapeUrlSequential(url))

  // Benchmark parallel method
  benchmark("Parallel", urls, (url: String) => scrapeUrlParallel(url))
}

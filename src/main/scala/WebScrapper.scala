import org.jsoup._
import org.jsoup.nodes._
import scala.collection.JavaConverters._

object WebScraper {
  def fetchArticles(url: String): List[String] = {
    val document = Jsoup.connect(url).get()
    val links = document.select("a[href]").asScala.map(_.attr("abs:href")).toList
    links
  }

  def bfs(urls: List[String]): List[String] = {
    var visited = Set[String]()
    var toVisit = urls

    while (toVisit.nonEmpty) {
      val url = toVisit.head
      toVisit = toVisit.tail
      if (!visited.contains(url)) {
        visited += url
        toVisit ++= fetchArticles(url)
      }
    }
    visited.toList
  }
}

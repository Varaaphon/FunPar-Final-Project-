import org.scalatest.flatspec.AnyFlatSpec

class WebScraperSpec extends AnyFlatSpec {
  "WebScraper" should "fetch articles from a given URL" in {
    val articles = WebScraper.fetchArticles("https://example.com")
    assert(articles.nonEmpty)
  }
}

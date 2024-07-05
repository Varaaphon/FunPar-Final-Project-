import org.openjdk.jmh.annotations._

@State(Scope.Benchmark)
class WebScraperBenchmark {
  @Benchmark
  def fetchArticlesBenchmark(): Unit = {
    WebScraper.fetchArticles("https://example.com")
  }
}

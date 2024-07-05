import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object DataProcessor {
  def processArticles(articles: List[String]): Future[List[(String, Int)]] = Future {
    articles.groupBy(identity).view.mapValues(_.size).toList
  }
}

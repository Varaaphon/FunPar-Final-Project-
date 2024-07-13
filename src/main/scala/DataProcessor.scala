object DataProcessor {
  def process(results: List[(String, String, String, String)]): Unit = {
    val groupedByAuthor = results.groupBy(_._3)
    val groupedByDate = results.sortBy(_._4).reverse
    val groupedByTitle = results.groupBy(_._2)

    // Output formatted results
    println("Author:")
    groupedByAuthor.foreach { case (author, articles) =>
      println(s" $author:")
      articles.foreach { case (url, _, _, _) =>
        println(s" ($url) - $author")
      }
    }

    println("\nPublication Date (arranged from most recent to oldest):")
    groupedByDate.foreach { case (url, _, _, date) =>
      println(s" ($url) - $date")
    }

    println("\nTitle:")
    groupedByTitle.foreach { case (title, articles) =>
      println(s" $title:")
      articles.foreach { case (url, _, _, _) =>
        println(s" ($url) - $title")
      }
    }
  }

  def formatResults(results: List[(String, String, String, String)]): String = {
    val groupedByAuthor = results.groupBy(_._3)
    val groupedByDate = results.sortBy(_._4).reverse
    val groupedByTitle = results.groupBy(_._2)

    val authorOutput = groupedByAuthor.map { case (author, articles) =>
      s"Author: $author\n${articles.map { case (url, _, _, _) => s"($url) - $author" }.mkString("\n")}\n"
    }.mkString("\n")

    val dateOutput = groupedByDate.map { case (url, _, _, date) =>
      s"Publication Date: ($url) - $date"
    }.mkString("\n")

    val titleOutput = groupedByTitle.map { case (title, articles) =>
      s"Title: $title\n${articles.map { case (url, _, _, _) => s"($url) - $title" }.mkString("\n")}\n"
    }.mkString("\n")

    s"$authorOutput\n$dateOutput\n$titleOutput"
  }
}

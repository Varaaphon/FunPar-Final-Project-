import plotly._
import plotly.element._
import plotly.layout._
import plotly.Plotly._

object Visualization {
  def createPlot(data: List[(String, Int)]): Unit = {
    val plot = Bar(
      data.map(_._1),
      data.map(_._2)
    )

    plot.plot(
      title = "Article Frequency",
      xaxis = Axis(title = "Article"),
      yaxis = Axis(title = "Frequency")
    )
  }
}

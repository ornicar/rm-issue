import scala.concurrent.Future
import reactivemongo.api.*

object Main extends App:
  val driver = new AsyncDriver
  val conn = driver.connect(List("localhost"))

  for i <- 0 to 40 do
    println(i)
    Thread sleep 1000

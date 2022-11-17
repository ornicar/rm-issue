import scala.concurrent.*
import scala.concurrent.duration.*
import reactivemongo.api.*

object Minimal extends App:
  given ExecutionContext = scala.concurrent.ExecutionContext.global

  val driver = new AsyncDriver
  val connection =
    Await.result(driver.connect(List("localhost:27017")), 2.seconds)
  val db = Await.result(connection.database("test"), 2.seconds)

  for i <- 0 to 32 do
    println(i)
    Thread sleep 1000

  driver.close()

import scala.concurrent.*
import scala.concurrent.duration.*
import reactivemongo.api.*

object Minimal extends App:
  run

def run =
  given ExecutionContext = scala.concurrent.ExecutionContext.global
  val driver = new AsyncDriver

  (1 to 2).toList map { i =>
    Await.result(
      for
        conn <- driver.connect(List("localhost:27017"))
        db <- conn.database(s"test-rm-$i")
      yield db,
      3.seconds
    )
  }

  for i <- 0 to 12 do
    println(i)
    Thread sleep 1000

  driver.close()

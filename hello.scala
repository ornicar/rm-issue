import scala.concurrent.*
import scala.concurrent.duration.*
import reactivemongo.api.*
import reactivemongo.api.bson.BSONDocument

object Hello extends App:

  run

def run =

  given ExecutionContext = ExecutionContext.global

  val driver = new AsyncDriver

  val hello = for
    conn <- driver.connect(List("localhost:27017"))
    db <- conn.database("test-rm")
  // java.lang.NoClassDefFoundError: Could not initialize class Hello$
  // at Hello$.given_ExecutionContext(hello.scala:9)
  yield ()

  Await.result(hello, 3.seconds)

  driver.close()

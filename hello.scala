import scala.concurrent.*
import ExecutionContext.global
import scala.concurrent.duration.*
import reactivemongo.api.*
import reactivemongo.api.bson.BSONDocument

object Hello extends App:

  given ExecutionContext = ExecutionContext.global

  val driver = new AsyncDriver

  val hello = for
    parsedUri <- MongoConnection.fromString("mongodb://localhost:27017/testrm")
    conn <- driver.connect(parsedUri)
    db <- conn.database("test-rm")
  // java.lang.NoClassDefFoundError: Could not initialize class Hello$
  // at Hello$.given_ExecutionContext(hello.scala:9)
  yield ()

  Await.result(hello, 3.seconds)

  driver.close()

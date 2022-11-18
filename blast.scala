import scala.concurrent.*
import scala.concurrent.duration.*
import reactivemongo.api.*
import reactivemongo.api.bson.BSONDocument

object Blast extends App:
  given ExecutionContext = scala.concurrent.ExecutionContext.global

  val driver = new AsyncDriver

  val timeout = 2.seconds
  val options = MongoConnectionOptions.default
  println(options)
  println(options.maxNonQueryableHeartbeats)
  val connection =
    Await.result(driver.connect(List("localhost:27017"), options), timeout)
  val db = Await.result(connection.database("test-rm"), timeout)
  val coll = db("test-rm1")

  val prepare =
    for
      _ <- coll.delete.one(BSONDocument())
      _ <- coll.insert.one(BSONDocument("i" -> 1))
    // the line above causes:
    // java.lang.NoClassDefFoundError: Could not initialize class Blast$
    // at Blast$.coll(blast.scala:18)
    // ????????!!!!!1!!1
    // and the test-rm db is not created
    yield ()

  Await.result(prepare, 3.seconds)
  println(coll)

  // val c = Await.result(coll, duration.Duration.Inf)
  // println(c)

  for i <- 0 to 2 do
    coll
      .find(BSONDocument())
      .cursor[BSONDocument]()
      .collect[List](-1, Cursor.FailOnError[List[BSONDocument]]())
      .map(_.size)
      .onComplete(println)
    println(i)
    Thread sleep 1000

  driver.close()

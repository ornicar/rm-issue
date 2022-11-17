import scala.concurrent.*
import scala.concurrent.duration.*
import reactivemongo.api.*
import reactivemongo.api.bson.BSONDocument

object Main2 extends App:
  given ExecutionContext = scala.concurrent.ExecutionContext.global

  val driver = new AsyncDriver

  val timeout = 2.seconds
  val options = MongoConnectionOptions.default
  println(options)
  println(options.maxNonQueryableHeartbeats)
  val connection =
    Await.result(driver.connect(List("localhost:27017"), options), timeout)
  val db = Await.result(connection.database("test"), timeout)
  val coll = db("test")

  println(coll)

  // val c = Await.result(coll, duration.Duration.Inf)
  // println(c)

  for i <- 0 to 32 do
    coll.find(BSONDocument()).one[BSONDocument].onComplete(println)
    println(i)
    Thread sleep 1000

  driver.close()

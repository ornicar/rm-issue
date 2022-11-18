import scala.concurrent.*
import scala.concurrent.duration.*
import reactivemongo.api.*
import reactivemongo.api.bson.*

object Blast extends App:
  run

def run =

  given ExecutionContext = scala.concurrent.ExecutionContext.global

  val driver = new AsyncDriver

  val options = MongoConnectionOptions.default
  println(options)
  println(options.maxNonQueryableHeartbeats)

  val colls = (1 to 3).toList map { i =>
    Await.result(
      for
        conn <- driver.connect(List("localhost:27017"), options)
        db <- conn.database(s"test-rm-$i")
        coll = db(s"test-rm-$i")
        _ <- coll.delete.one(BSONDocument())
        _ <- coll.insert.many(
          (1 to 1000).toList.map(i => BSONDocument("i" -> i))
        )
      yield coll,
      3.seconds
    )
  }

  // val c = Await.result(coll, duration.Duration.Inf)
  // println(c)

  def doStuff(coll: collection.BSONCollection): Future[Unit] =
    coll
      .find(BSONDocument())
      .cursor[BSONDocument]()
      .collect[List](-1, Cursor.FailOnError[List[BSONDocument]]())
      .map(_.size)
      .flatMap(_ => doStuff(coll))

  colls.map(doStuff)

  for i <- 0 to 32 do
    println(i)
    Thread sleep 1000

  driver.close()

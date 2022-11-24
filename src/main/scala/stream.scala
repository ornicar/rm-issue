import scala.concurrent.*
import scala.concurrent.duration.*
import reactivemongo.api.*
import reactivemongo.api.bson.*
import reactivemongo.akkastream.{State, cursorProducer}
import akka.actor.*
import akka.stream.*
import akka.stream.scaladsl.*

object Stream:
  def main(args: Array[String]): Unit = args match
    case Array("primary", uri, db) => testPrimary(uri, db)
    case Array(uri, db)            => testSecondary(uri, db)
    case _                         => println("Usage: <uri> <db>")

  def connectAndInsert(uri: String, dbName: String, nbDocs: Int)(using
      ExecutionContext
  ) =
    for
      parsedUri <- MongoConnection.fromString(uri)
      driver = new AsyncDriver
      conn <- driver.connect(parsedUri)
      db <- conn.database(dbName)
      coll = db("rmbug2")
      _ <- coll.delete.one(BSONDocument())
      _ = println(s"Inserting $nbDocs docs...")
      _ <- coll.insert.many((1 to nbDocs).map(i => BSONDocument("_id" -> i)))
      _ = println("Done.")
    yield (driver, coll)

  def testSecondary(uri: String, dbName: String, nbDocs: Int = 2000) =
    given ec: ExecutionContext = scala.concurrent.ExecutionContext.global
    given system: ActorSystem =
      ActorSystem(name = "rmtest", defaultExecutionContext = Some(ec))
    given Materializer = ActorMaterializer.create(system)
    Await.result(
      for
        (driver, coll) <- connectAndInsert(uri, dbName, nbDocs)
        _ <- coll
          .find(BSONDocument(), Some(BSONDocument("_id" -> true)))
          .cursor[BSONDocument](ReadPreference.secondary)
          .documentSource(nbDocs)
          .mapConcat(d => d.int("_id").toList)
          .throttle(50, 1.second)
          .runWith(Sink.foreach(println))
        _ <- driver.close()
        _ <- system.terminate()
      yield (),
      300.seconds
    )

  def testPrimary(uri: String, dbName: String, nbDocs: Int = 200_000) =
    given ec: ExecutionContext = scala.concurrent.ExecutionContext.global
    given system: ActorSystem =
      ActorSystem(name = "rmtest", defaultExecutionContext = Some(ec))
    given Materializer = ActorMaterializer.create(system)
    Await.result(
      for
        (driver, coll) <- connectAndInsert(uri, dbName, nbDocs)
        nb <- coll
          .find(BSONDocument(), Some(BSONDocument("_id" -> true)))
          .cursor[BSONDocument](ReadPreference.primary)
          .documentSource(nbDocs)
          .toMat(Sink.fold[Int, BSONDocument](0) { case (total, doc) =>
            println(s"$total ${doc int "_id"}")
            total + 1
          })(Keep.right)
          .run()
        _ = println(s"Done: $nb")
        _ <- driver.close()
        _ <- system.terminate()
      yield (),
      300.seconds
    )

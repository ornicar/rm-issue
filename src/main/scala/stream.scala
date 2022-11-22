import scala.concurrent.*
import scala.concurrent.duration.*
import reactivemongo.api.*
import reactivemongo.api.bson.*
import reactivemongo.akkastream.{State, cursorProducer}
import akka.actor.*
import akka.stream.*
import akka.stream.scaladsl.{Sink, Source}

object Stream:
  def main(args: Array[String]): Unit = args match
    case Array(uri, db) => letsgo(uri, db)
    case _              => println("Usage: <uri> <db>")

  def letsgo(uri: String, dbName: String, collName: String = "rmbug2") =
    given ec: ExecutionContext = scala.concurrent.ExecutionContext.global

    given system: ActorSystem =
      ActorSystem(name = "rmtest", defaultExecutionContext = Some(ec))

    given Materializer = ActorMaterializer.create(system)

    val nbDocs = 2000

    def stream(coll: collection.BSONCollection) = coll
      .find(BSONDocument(), Some(BSONDocument("_id" -> true)))
      .cursor[BSONDocument](ReadPreference.secondary)
      .documentSource(nbDocs)
      .mapConcat(d => d.int("_id").toList)
      .throttle(50, 1.second)
      .runWith(Sink.foreach(println))

    val run = for
      parsedUri <- MongoConnection.fromString(uri)
      driver = new AsyncDriver
      conn <- driver.connect(parsedUri)
      db <- conn.database(dbName)
      coll = db(collName)
      _ <- coll.delete.one(BSONDocument())
      _ <- coll.insert.many((1 to nbDocs).map(i => BSONDocument("_id" -> i)))
      _ <- stream(db(collName))
      _ <- driver.close()
      _ <- system.terminate()
    yield ()

    Await.result(run, 300.seconds)

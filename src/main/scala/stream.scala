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

  def letsgo(uri: String, dbName: String) =
    given ec: ExecutionContext = scala.concurrent.ExecutionContext.global

    given system: ActorSystem =
      ActorSystem(name = "rmtest", defaultExecutionContext = Some(ec))

    given Materializer = ActorMaterializer.create(system)

    def stream(coll: collection.BSONCollection) = coll
      .find(BSONDocument(), Some(BSONDocument("username" -> true)))
      .cursor[BSONDocument](ReadPreference.secondaryPreferred)
      .documentSource(5000)
      .mapConcat(d => d.string("username").toList)
      .zipWithIndex
      .throttle(50, 1.second)
      .runWith(Sink.foreach((u, i) => println(s"$i $u")))

    val run = for
      parsedUri <- MongoConnection.fromString(uri)
      driver = new AsyncDriver
      conn <- driver.connect(parsedUri)
      db <- conn.database(dbName)
      _ <- stream(db("user4"))
      _ <- driver.close()
      _ <- system.terminate()
    yield ()

    Await.result(run, 300.seconds)

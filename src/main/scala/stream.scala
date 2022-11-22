import scala.concurrent.*
import scala.concurrent.duration.*
import reactivemongo.api.*
import reactivemongo.api.bson.*
import reactivemongo.akkastream.{State, cursorProducer}
import akka.actor.*
import akka.stream.*
import akka.stream.scaladsl.{Sink, Source}

object Stream:
  def main(args: Array[String]): Unit =
    letsgo(args(0), args(1))

  def letsgo(uri: String, dbName: String) =
    given ec: ExecutionContext = scala.concurrent.ExecutionContext.global
    val driver = new AsyncDriver

    val db = Await.result(
      for
        parsedUri <- MongoConnection.fromString(uri)
        conn <- driver.connect(parsedUri)
        db <- conn.database(dbName)
      yield db,
      3.seconds
    )

    implicit val system: ActorSystem = ActorSystem(
      name = "akkastream-cursor",
      defaultExecutionContext = Some(ec)
    )

    implicit val materializer: Materializer = ActorMaterializer.create(system)

    val stream = db("user4")
      .find(BSONDocument(), Some(BSONDocument("username" -> true)))
      .cursor[BSONDocument]()
      .documentSource(500)
      .mapConcat(d => d.string("username").toList)
      .zipWithIndex
      .throttle(30, 1.second)
      .runWith(Sink.foreach((u, i) => println(s"$i $u")))

    Await.result(stream, 30.seconds)

    driver.close()

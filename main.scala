import scala.concurrent.Future
import reactivemongo.api.*

// object Main extends App:
//   val driver = new AsyncDriver
//   val conn = driver.connect(List("mongodb://localhost:27017/test?appName=rm-test"))

//   for i <- 0 to 600 do
//     println(i)
//     Thread sleep 1000

/*
[info] running Main
[sbt-bg-threads-9] INFO reactivemongo.api.Driver - No mongo-async-driver configuration found
[sbt-bg-threads-9] INFO reactivemongo.api.Driver - [Supervisor-1] Creating connection: Connection-1
0
[reactivemongo-akka.actor.default-dispatcher-4] INFO reactivemongo.core.actors.MongoDBSystem - [Supervisor-1/Connection-1] Starting the MongoDBSystem
[reactivemongo-akka.actor.default-dispatcher-4] INFO reactivemongo.core.netty.Pack - Instantiated reactivemongo.core.netty.Pack
1
2
3
4
5
6
7
8
9
10
11
12
13
14
15
16
17
18
19
20
21
22
23
24
25
26
27
28
29
30
[nioEventLoopGroup-2-1] WARN reactivemongo.core.protocol.MongoHandler - [Supervisor-1/Connection-1] Channel is closed under 1756017ns: localhost/127.0.0.1:27017; Please check network connectivity and the status of the set. (channel [id: 0xc3ee0cec, L:/127.0.0.1:44742 ! R:localhost/127.0.0.1:27017])
[reactivemongo-akka.actor.default-dispatcher-4] WARN reactivemongo.core.actors.MongoDBSystem - [Supervisor-1/Connection-1] The entire node set is unreachable, is there a network problem?
31
32
33
34
35
36
37
38
39
40
[success] Total time: 41 s, completed Nov 17, 2022, 12:41:59 PM
*/

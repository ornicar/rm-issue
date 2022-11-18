Demonstrate bug in 1.1.0-RC6
-----------

**Environment:**
- scala 3.2.1 (same is observed with scala 2.13)
- reactivemongo 1.1.0-RC6
- mongodb 5.0.13

`minimal.scala` establishes a connection then awaits 32 seconds.
At 30s, reactivemongo logs errors:
```
[nioEventLoopGroup-2-1] WARN reactivemongo.core.protocol.MongoHandler - [Supervisor-1/Connection-1] Channel is closed under 1301956ns: localhost/127.0.0.1:27017; Please check network connectivity and the status of the set. (channel [id: 0x618a31a9, L:/127.0.0.1:41538 ! R:localhost/127.0.0.1:27017])
[reactivemongo-akka.actor.default-dispatcher-8] WARN reactivemongo.core.actors.MongoDBSystem - [Supervisor-1/Connection-1] The entire node set is unreachable, is there a network problem?
```
In practice it also results in failed queries. Here we provide a minimal reproduction environment.

```
sbt "runMain Minimal"
```

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

```
[info] running Minimal
[sbt-bg-threads-1] INFO reactivemongo.api.Driver - [Supervisor-1] Creating connection: Connection-1
[reactivemongo-akka.actor.default-dispatcher-4] INFO reactivemongo.core.actors.MongoDBSystem - [Supervisor-1/Connection-1] Starting the MongoDBSystem
[reactivemongo-akka.actor.default-dispatcher-4] INFO reactivemongo.core.netty.Pack - Instantiated reactivemongo.core.netty.Pack
0
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
[nioEventLoopGroup-2-1] WARN reactivemongo.core.protocol.MongoHandler - [Supervisor-1/Connection-1] Channel is closed under 1301956ns: localhost/127.0.0.1:27017; Please check network connectivity and the status of the set. (channel [id: 0x618a31a9, L:/127.0.0.1:41538 ! R:localhost/127.0.0.1:27017])
[reactivemongo-akka.actor.default-dispatcher-8] WARN reactivemongo.core.actors.MongoDBSystem - [Supervisor-1/Connection-1] The entire node set is unreachable, is there a network problem?
30
31
32
```

Now, here where it gets fun. It might happen at the 30s mark. Or it might be later - probably on a multiple of 30.

IDK yet why.

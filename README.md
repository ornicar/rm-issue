`cursor id XXX not found` when streaming data from secondary
----------------------

To reproduce you need a replicaset with at least one secondary.

```
sbt run "mongodb://primary:27017,secondary:27017" dbname
```

The test will create a dbname.rmbug2 collection, empty it, and insert 2000 documents.

Then it will stream the documents from mongodb to stdout.

Expected: should print 2000 documents.
Issue: After about 100 documents, the stream closes with an error.

Output:
```
[...]
223
236
237
255
319
348
373
387
394
395
397
463
465
495
499
29
39
41
67
87
Exception in thread "main" DatabaseException['cursor id 8910965325409551864 not found' (code = 43)]
```

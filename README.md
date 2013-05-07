# Tailable Cursors through WebSockets with ReactiveMongo and Play 2

ReactiveMongo allows to enumerate a MongoDB cursor in a non-blocking and asynchronous way.

This demo shows how to stream the documents that are inserted into a capped collection through a WebSocket, using the [ReactiveMongo Play Plugin](https://github.com/zenexity/Play-ReactiveMongo).
It demonstrates the following features:
+ Tailable Cursors with ReactiveMongo
+ Creating capped collections
+ Cursor Enumerators
+ Non-blocking I/O
+ Integration with Play JSON library (included in the Play ReactiveMongo Plugin)
+ Integration of ReactiveMongo with Play 2 WebSocket API.

When run, this sample allows to submit new documents via a WebSocket and inserts them into the Capped Collection. This Capped Collection is enumerated through the same Websocket, so any new document is eventually sent back to the client.

```scala
def watchCollection = WebSocket.using[JsValue] { request =>
  val collection = db.collection[JSONCollection]("acappedcollection")
  // Inserts the received messages into the capped collection
  val in = Iteratee.flatten(futureCollection.map(collection => Iteratee.foreach[JsValue] { json =>
    println("received " + json)
    collection.insert(json)
  }))

  // Enumerates the capped collection
  val out = {
    val futureEnumerator = futureCollection.map { collection =>
      // so we are sure that the collection exists and is a capped one
      val cursor: Cursor[JsValue] = collection
        // we want all the documents
        .find(Json.obj())
        // the cursor must be tailable and await data
        .options(QueryOpts().tailable.awaitData)
        .cursor[JsValue]

      // ok, let's enumerate it
      cursor.enumerate
    }
    Enumerator.flatten(futureEnumerator)
  }

  // We're done!
  (in, out)
}
```

Get the complete example and run it! It uses Play 2.1, ReactiveMongo master and Play ReactiveMongo Plugin.

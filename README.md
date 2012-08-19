# Tailable Cursors through WebSockets with ReactiveMongo and Play 2

ReactiveMongo allows to enumerate a MongoDB cursor in a non-blocking and asynchronous way, using Play's Iteratee library.

This demo shows how to stream the documents that are inserted into a capped collection through a WebSocket.
It demonstrates the following features:
+ Tailable Cursors with MongoAsync
+ Cursor Enumerators
+ Non-blocking I/O
+ Integration with Play JSON library (included in the Play ReactiveMongo Plugin)
+ Integration of MongoAsync with Play 2 WebSocket API.

When run, this sample allows to submit new documents via a WebSocket and inserts them into the Capped Collection. This Capped Collection is enumerated through the same Websocket, so any new document is eventually sent back to the client.

```scala
    val tailableFlags = QueryFlags.TailableCursor | QueryFlags.AwaitData
    
    def watchCollection = WebSocket.using[JsValue] { request => 
      val coll = MongoAsyncPlugin.collection("acappedcollection")
      
      // Inserts the received messages into the capped collection
      val in = Iteratee.foreach[JsValue] { json =>
        println("received " + json)
        coll.insert(json)
      }
      // Enumerates the capped collection
      val out = Cursor.enumerate(
        coll.find[JsValue, JsValue, JsValue](Json.obj(), flags = tailableFlags))
      
      // We're done!
      (in, out)
    }
```

Get the complete example and run it! It uses Play 2.1 master, ReactiveMongo master and Play ReactiveMongo Plugin.
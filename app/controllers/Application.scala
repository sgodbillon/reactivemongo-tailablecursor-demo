package controllers

import play.api._
import play.api.mvc._
import play.api.Play.current
import play.api.libs.json._
import play.api.libs.concurrent._
import play.api.libs.iteratee._

import play.modules.mongodb._
import play.modules.mongodb.PlayBsonImplicits._

import org.asyncmongo.api._
import org.asyncmongo.handlers.{BSONReaderHandler, BSONWriter, BSONReader}
import org.asyncmongo.handlers.DefaultBSONHandlers._
import org.asyncmongo.protocol._

object Application extends Controller {
  
  def index = Action {
    Ok(views.html.index())
  }

  def watchCollection = WebSocket.using[JsValue] { request => 
    val coll = MongoAsyncPlugin.collection("acappedcollection")
    // Inserts the received messages into the capped collection
    val in = Iteratee.foreach[JsValue] { json =>
      println("received " + json)
      coll.insert(json)
    }
    // Enumerates the capped collection
    val out = Cursor.enumerate(coll.find[JsValue, JsValue, JsValue](Json.obj(), None, 0, 0, QueryFlags.TailableCursor | QueryFlags.AwaitData))
    (in, out)
  }
}
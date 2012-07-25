/*
 * Copyright 2012 Stephane Godbillon
 *
 * This sample is in the public domain.
 */
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

import akka.util.Timeout
import akka.util.duration._

object Application extends Controller {
  def index = Action {
    Ok(views.html.index())
  }

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
      // make sure the primary has been discovered.
      coll.connection.waitForPrimary(Timeout(1 seconds)).flatMap(_ =>
        coll.find[JsValue, JsValue, JsValue](Json.obj(), flags = tailableFlags)))

    // We're done!
    (in, out)
  }
}
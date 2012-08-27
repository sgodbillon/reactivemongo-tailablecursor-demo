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
import play.api.libs.iteratee._

import play.modules.reactivemongo._
import play.modules.reactivemongo.PlayBsonImplicits._

import reactivemongo.api._
import reactivemongo.bson.handlers.DefaultBSONHandlers.DefaultBSONReaderHandler

import scala.concurrent.{ExecutionContext, Future}

object Application extends Controller with MongoController {

  def index = Action {
    Ok(views.html.index())
  }

  def watchCollection = WebSocket.using[JsValue] { request =>
    val coll = ReactiveMongoPlugin.collection("acappedcollection")

    // Inserts the received messages into the capped collection
    val in = Iteratee.foreach[JsValue] { json =>
      println("received " + json)
      coll.insert(json)
    }

    // Enumerates the capped collection
    val out = {
      val cursor = coll.find(Json.obj(), QueryOpts().tailable.awaitData)
      cursor.enumerate
    }

    // We're done!
    (in, out)
  }
}
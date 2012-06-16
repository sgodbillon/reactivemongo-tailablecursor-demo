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
import akka.dispatch.Future
import akka.util.duration._

object Application extends Controller {
  
  def index = Action {
    Ok(views.html.index())
  }

  def watchCollection = WebSocket.using[JsValue] { request => 
    val coll = MongoAsyncPlugin.collection("acappedcollection")
    val in = Iteratee.foreach[JsValue] { json =>
      println("received " + json)
      coll.insert(json)
    }
    val out = enumerate(Some(coll.find[JsValue, JsValue, JsValue](Json.obj(), None, 0, 0, QueryFlags.TailableCursor | QueryFlags.AwaitData)))
    (in, out)
  }

  def enumerate[T](futureCursor: Option[Future[Cursor[T]]]) :Enumerator[T] = {
    var currentCursor :Option[Cursor[T]] = None
    Enumerator.generateM {
      if(currentCursor.isDefined && currentCursor.get.iterator.hasNext){
        Promise.pure(Some(currentCursor.get.iterator.next))
      } else if(currentCursor.isDefined && currentCursor.get.hasNext) {
        val p = Promise[Option[T]]()
        def periodicChecker(cursor: Cursor[T]) :Unit = {
          if(cursor.iterator.hasNext) {
            currentCursor = Some(cursor)
            p.redeem(Some(cursor.iterator.next))
          } else {
            play.core.Invoker.system.scheduler.scheduleOnce(500 milliseconds)({
              currentCursor.get.next.get.onSuccess {
                case yop => periodicChecker(yop)
              }
            })
          }
        }
        periodicChecker(currentCursor.get)
        p
      } else if(!currentCursor.isDefined && futureCursor.isDefined) {
        new AkkaPromise(futureCursor.get.map { cursor =>
          println("redeemed from first cursor")
          currentCursor = Some(cursor)
          if(cursor.iterator.hasNext) {
            Some(cursor.iterator.next)
          }
          else {
            None
          }
        })
      } else {
        println("Nothing to enumerate")
        Promise.pure(None)
      }
    }
  }
}
package controllers

import java.util.{Calendar, TimeZone}

import javax.inject._
import play.api._
import play.api.data._
import play.api.libs.json._

import scala.concurrent.Await
import scala.concurrent.duration._
import play.api.libs.ws._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.io
import javax.inject.Inject
import play.api.mvc._
import model.RouteForm._
import model.ConnectionJsonModel._
import play.api.http.Status
import scala.collection.mutable._


/**
  * This controller creates an `Action` to handle HTTP requests to the
  * application's home page.
  */
@Singleton
class RoutePlannerController  @Inject()(cc: MessagesControllerComponents, ws: WSClient) extends MessagesAbstractController(cc)  {

  val baseRequest: WSRequest = ws.url("https://api.irail.be/connections")
  private val connections = ArrayBuffer[Connection]()


  // URL to the method to calculate the departures.  You can call this directly from the template, but it
  // can be more convenient to leave the template completely stateless
  private val postURL = routes.RoutePlannerController.calculateConnections()


  def index() = Action { implicit request: MessagesRequest[AnyContent] =>
    Ok(views.html.index(form, connections, postURL))
  }


  // This will be the action that handles our form post
  def calculateConnections() = Action { implicit request: MessagesRequest[AnyContent] =>
    val errorFunction = { formWithErrors: Form[Data] =>
      println("error")
      // This is the bad case, where the form had validation errors.
      // Let's show the user the form again, with the errors highlighted.
      // Note how we pass the form with errors to the template.
      BadRequest(views.html.index(formWithErrors, connections, postURL))
    }


    val successFunction = { data: Data =>

      val request = baseRequest.addQueryStringParameters("from" -> data.from, "to" -> data.to,"format" -> "json")
      val f = request.get().map({
        response =>
          /* To prevent the whole system from crashing if we were to have an error return. */
          if (response.status == 200){
          val connectionsResult = (response.json \ "connection").get.validate[List[Connection]]
          connections.clear()
          connectionsResult.foreach(c => c.foreach(updateAndAddConnection))
          }
      })

      Await.result(f, 30 second)
      Redirect(routes.RoutePlannerController.index()).flashing("Routes" -> (data.from +" to "+  data.to))
    }


    val formValidationResult = form.bindFromRequest
    formValidationResult.fold(errorFunction, successFunction)
  }

  def updateAndAddConnection(connection: Connection) : Unit = {

    val departureTime = TimeUtils.getTime(connection.departure.time.toLong)
    val arrivalTime =  TimeUtils.getTime(connection.arrival.time.toLong)
    val duration =  (connection.duration.toLong/60).toInt.toString
    val vehicleDep =  connection.departure.vehicle.substring(8)
    val vehicleArri =  connection.arrival.vehicle.substring(8)


    val departure = connection.departure.copy(time=departureTime, vehicle = vehicleDep)
    val arrival = connection.arrival.copy(time=arrivalTime,  vehicle = vehicleArri)

    val updatedConnection =  connection.copy(duration=duration, departure=departure, arrival=arrival )
    //debugger
    println("updated connection: ", updatedConnection)
    connections.append(updatedConnection)
  }

}





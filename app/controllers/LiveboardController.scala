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
import model.LiveForm._
import model.ConnectionJsonModel._

import scala.collection.mutable._


/**
  * This controller creates an `Action` to handle HTTP requests to the
  * application's home page.
  */
@Singleton
class LiveboardController  @Inject()(cc: MessagesControllerComponents, ws: WSClient) extends MessagesAbstractController(cc)  {
  //The basic link to where requests are to be sent.
  val baseRequest: WSRequest = ws.url("https://api.irail.be/liveboard/?station=")
  private val departures = ArrayBuffer[LiveboardDeparture]()

  // URL to the method to calculate the departures.  You can call this directly from the template, but it
  // can be more convenient to leave the template completely stateless
  private val postURL = routes.LiveboardController.calculateConnections()


  def liveboard() = Action { implicit request: MessagesRequest[AnyContent] =>
    Ok(views.html.liveboard(form, departures, postURL))
  }

  def liveboardWithStation(station: String) = Action {request: MessagesRequest[AnyContent] =>
    val request = baseRequest.addQueryStringParameters("station" -> station, "fast" -> "true" ,"format" -> "json")

    val f = request.get()map({
      response =>

        println("response.json: ", response.json)
        var liveboardDepartureResult = (response.json \ "departures" \ "departure").validate[List[LiveboardDeparture]]
        departures.clear()
        liveboardDepartureResult.foreach(d => d.foreach(updateAndAddDeparture))
    })

    Await.result(f, 30 second)
    Redirect(routes.LiveboardController.liveboard()).flashing("Routes" -> (station))

  }


  // This will be the action that handles our form post
  def calculateConnections() = Action { implicit request: MessagesRequest[AnyContent] =>
    //For if the form given has errors.
    val errorFunction = { formWithErrors: Form[Data] =>
      // This is the bad case, where the form had validation errors.
      // Let's show the user the form again, with the errors highlighted.
      // Note how we pass the form with errors to the template.
      BadRequest(views.html.liveboard(formWithErrors, departures, postURL))
    }

    val successFunction = { data: Data =>

      val request = baseRequest.addQueryStringParameters("station" -> data.station, "fast" -> "true" ,"format" -> "json")

      val f = request.get()map({
        response =>
          println("succes liveboard: ", (response.json \ "departures" \ "departure"))

        var liveboardDepartureResult = (response.json \ "departures" \ "departure").validate[List[LiveboardDeparture]]
        departures.clear()
        liveboardDepartureResult.foreach(d => d.foreach(updateAndAddDeparture))
      })

      Await.result(f, 30 second)
      Redirect(routes.LiveboardController.liveboard()).flashing("Routes" -> (data.station))
    }


    val formValidationResult = form.bindFromRequest
    formValidationResult.fold(errorFunction, successFunction)
  }

  def updateAndAddDeparture(departure: LiveboardDeparture) : Unit = {

    //Get the information we need.
    val vehicle = departure.vehicle.substring(8)
    val delay = departure.delay
    val time = TimeUtils.getTime(departure.time.toLong)
    val destination = departure.destination
    val platform = departure.platform

    //Copy of the liveboard departure.
    val updatedDeparture =  departure.copy(vehicle=vehicle, delay=delay, time=time, destination=destination, platform=platform)

    departures.append(updatedDeparture)
  }



}
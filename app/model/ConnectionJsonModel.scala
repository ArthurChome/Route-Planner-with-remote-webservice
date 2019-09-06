package model


import play.api.libs.json._
import play.api.libs.functional.syntax._

object ConnectionJsonModel {

  case class LiveboardDeparture(vehicle: String, delay: String, time: String, destination: String, platform: String)
  case class Liveboard(station: String, departures: List[Departure])
  case class Connection(id: String, departure: Departure, arrival: Arrival, duration: String)
  case class Departure(station: String, time: String, vehicle: String, platform: String )
  case class Arrival(station: String, time: String, vehicle: String, platform: String)
  //case class Board

  implicit val liveboardDepartureReads: Reads[LiveboardDeparture] = (
    (JsPath \ "vehicle").read[String] and
    (JsPath \ "delay").read[String] and
    (JsPath \ "time").read[String] and
    (JsPath \ "station").read[String] and
    (JsPath \ "platform").read[String]
  )(LiveboardDeparture.apply _)

  implicit val departureReads: Reads[Departure] = (
    (JsPath \ "station").read[String] and
      (JsPath \ "time").read[String] and
      (JsPath \ "vehicle").read[String] and
      (JsPath \ "platform").read[String]
    )(Departure.apply _)

  implicit val arrivalReads: Reads[Arrival] = (
    (JsPath \ "station").read[String] and
      (JsPath \ "time").read[String] and
      (JsPath \ "vehicle").read[String] and
      (JsPath \ "platform").read[String]
    )(Arrival.apply _)

  implicit val connectionReads: Reads[Connection] = (
    (JsPath \ "id").read[String] and
      (JsPath \ "departure").read[Departure] and
      (JsPath \ "arrival").read[Arrival] and
      (JsPath \ "duration").read[String]

    )(Connection.apply _)

}



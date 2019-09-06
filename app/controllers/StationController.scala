package controllers

import javax.inject.{Inject, _}
import play.api._
import play.api.libs.json._
import play.api.libs.ws._
import play.api.mvc._
import scala.io


@Singleton
class StationController  @Inject()(cc: MessagesControllerComponents, ws: WSClient) extends MessagesAbstractController(cc)  {

  var stationsFile = Environment.simple().getFile("/public/data/stations.csv")
  var stationsList = io.Source.fromFile(stationsFile).getLines.toList


  // This will be the action that handles our ajax request
  def getStationNames(term: String) = Action { implicit request: Request[AnyContent] =>
    val suggestions = stationsList.filter(_.toLowerCase.startsWith(term.toLowerCase))
    Ok(Json.toJson(suggestions))

  }

}





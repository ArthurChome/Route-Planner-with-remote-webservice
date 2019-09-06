package controllers

import javax.inject.Inject
import play.api.mvc._
import play.api.routing._

class Application @Inject()(components: ControllerComponents) extends AbstractController(components) {

  def javascriptRoutes = Action { implicit request =>
    Ok(
      JavaScriptReverseRouter("jsRoutes")(
        routes.javascript.StationController.getStationNames,
      )
    ).as("text/javascript")
  }

}

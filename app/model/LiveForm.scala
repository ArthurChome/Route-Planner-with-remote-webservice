package model

object LiveForm {

  import play.api.data.Form
  import play.api.data.Forms._

  /**
    * A form processing DTO that maps to the form below.
    *
    * Using a class specifically for form binding reduces the chances
    * of a parameter tampering attack and makes code clearer.
    */
  case class Data(station: String)

  /**
    * The form definition for the "create a route" form.
    * It specifies the form fields and their types,
    * as well as how to convert from a RouteData to form data and vice versa.
    */
  val form = Form(
    mapping(
      "station" -> nonEmptyText
    )(Data.apply)(Data.unapply)
  )



}

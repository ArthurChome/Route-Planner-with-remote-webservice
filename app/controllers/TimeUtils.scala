package controllers

import java.util.{Calendar, TimeZone}

object TimeUtils {
  val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))

  def getTime(timestamp: Long) : String = {
    calendar.setTime(new java.util.Date(timestamp*1000))
    val hour = calendar.get(Calendar.HOUR_OF_DAY)+1
    val m = calendar.get(Calendar.MINUTE)
    var min = ""
    if(m < 10){
      min = "0" + m
    }else {
      min = m.toString
    }
    return hour + ":" +min
  }
}

package controllers

import javax.inject._
import play.api.mvc._
import org.json.JSONObject
import com.google.gson.Gson
import java.net.{URL, HttpURLConnection}

/**
 * Below file provides HomeController for the Weather app
 * @param controllerComponents constructor parameter that can be injected using constructor injection for BaseController
 */
@Singleton
class HomeController @Inject()(val controllerComponents: ControllerComponents) extends BaseController {

  case class WeatherOutput(temp: Double, pressure: Double, umbrella: Boolean)

  /**
   * Following method takes name of a city as input and displays the weather data in format shown below
   *  {
        "temp": 7,
        "pressure": 1234,
        "umbrella": true
      }
   * @param city name of the city we want to check the weather output for
   * @return Display action
   */
  def getWeatherData(city: String): Action[AnyContent] = Action { implicit request =>

    // construct URL
    val url = "http://api.openweathermap.org/data/2.5/weather?q="+city+"&appid=8b8338eb2dac23ac219c0e70360a82f3"

    // get content from URL
    try{
      val content: String = getRestContent(url)

      // extract required properties from the content
      val jsonObject = new JSONObject(content)
      val mainWeatherObject = jsonObject.getJSONObject("main")
      val temp = mainWeatherObject.getDouble("temp")
      val pressure = mainWeatherObject.getDouble("pressure")
      val weatherJsonArray = jsonObject.getJSONArray("weather")
      val weatherObject = weatherJsonArray.getJSONObject(0)
      val weatherState = weatherObject.getString("main")
      var carryUmbrellaFlag = false
      if(weatherState == "Thunderstorm" ||weatherState == "Drizzle" ||weatherState == "Rain"){
        carryUmbrellaFlag = true
      }

      // construct required output
      val finalWeatherOutput = WeatherOutput(temp, pressure, carryUmbrellaFlag)
      // using gson to stringify the json
      val gson = new Gson
      val weatherOutputJson = gson.toJson(finalWeatherOutput)

      // display result
      Ok(views.html.index(weatherOutputJson))
    } catch {
      case ioe: java.io.IOException =>
        println("IO Exception error")
        println(ioe)
        Ok(views.html.index(ioe.toString))
      case ste: java.net.SocketTimeoutException =>
        println("Socket timeout error")
        println(ste)
        Ok(views.html.index(ste.toString))
    }


  }

  /**
   * The following method returns the text content from URL as a String
   * @param url The full url to connect to
   * @param connectTimeout  Sets a specified timeout value, in milliseconds,
   * to be used when opening a communications link to the resource referenced
   * by this URLConnection.
   * If the timeout expires before the connection can
   * be established, a java.net.SocketTimeoutException is raised.
   * A timeout of zero is interpreted as an infinite timeout.
   * Defaults to 5000 ms.
   * @param readTimeout
   * If the timeout expires before there is data available
   * for read, a java.net.SocketTimeoutException is raised.
   * A timeout of zero is interpreted as an infinite timeout.
   * Defaults to 5000 ms.
   * @param requestMethod Defaults to "GET"
   * @return text content as String
   */
  @throws(classOf[java.io.IOException])
  @throws(classOf[java.net.SocketTimeoutException])
  def getRestContent(url: String,
                     connectTimeout: Int = 5000,
                     readTimeout: Int = 5000,
                     requestMethod: String = "GET"): String =
  {
    // create connection object and set properties
    val connection = new URL(url).openConnection.asInstanceOf[HttpURLConnection]
    connection.setConnectTimeout(connectTimeout)
    connection.setReadTimeout(readTimeout)
    connection.setRequestMethod(requestMethod)

    // obtain the required data
    val inputStream = connection.getInputStream
    val content = scala.io.Source.fromInputStream(inputStream).mkString
    if (inputStream != null) inputStream.close()
    content
  }
}

package controllers

import org.scalatestplus.play._
import org.scalatestplus.play.guice._
import play.api.test._
import play.api.test.Helpers._

/**
 * Add your spec here.
 * You can mock out a whole application including requests, plugins etc.
 *
 * For more information, see https://www.playframework.com/documentation/latest/ScalaTestingWithScalaTest
 */
class HomeControllerSpec extends PlaySpec with GuiceOneAppPerTest with Injecting {

  "HomeController GET" should {

    "render the index page that prints sum of two numbers" in {
      val request = FakeRequest(GET, "/current/Berlin")
      val weatherUpdate = route(app, request).get

      status(weatherUpdate) mustBe OK
      contentType(weatherUpdate) mustBe Some("text/html")
      contentAsString(weatherUpdate) must include ("temp")
      contentAsString(weatherUpdate) must include ("pressure")
      contentAsString(weatherUpdate) must include ("umbrella")

    }

  }
}

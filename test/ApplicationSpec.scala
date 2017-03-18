
import play.api.test._

class ApplicationSpec extends PlaySpecification {

  "Application" should {

    "send 404 on a bad request" in new MockedApplication {

      val result = route(app, FakeRequest(GET, "/boum")).get
      status(result) must_== 404
    }

    "render the index page" in new MockedApplication {
      val home = route(app, FakeRequest(GET, "/")).get

      status(home) must equalTo(OK)
      contentType(home) must beSome.which(_ == "text/html")
      contentAsString(home) must contain ("Your new application is ready.")
    }
  }
}

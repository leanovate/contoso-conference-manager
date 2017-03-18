import org.openqa.selenium.WebDriver
import play.api.test._
import play.api.{ApplicationLoader, Environment, Mode}

class MockedApplication
    extends WithApplicationLoader(
      new MockedApplicationLoader)

class MockedApplicationWithBrowser[W <: WebDriver]
    extends WithBrowser[W](
      app = new MockedApplicationLoader().load(
        ApplicationLoader.createContext(
          new Environment(
            new java.io.File("."),
            ApplicationLoader.getClass.getClassLoader,
            Mode.Test))))

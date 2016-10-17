package deployments

import scala.scalajs._
import scala.scalajs.js.annotation.JSExport
import org.scalajs.dom._
import org.scalajs.dom.ext.Ajax
import scala.scalajs.js.timers._

import scalajs.concurrent.JSExecutionContext.Implicits.queue
import japgolly.scalajs.react._
import japgolly.scalajs.react.extra.router._
import japgolly.scalajs.react.vdom.prefix_<^._

import io.circe.Json
import io.circe.parser._
import io.circe.generic.auto._

@JSExport
object ReactApp {

  sealed trait Page
  case object Home extends Page
  case object DeploymentsList extends Page
  case object ApiKeyManagement extends Page

  case class State(
    deployments: Seq[Deployment]
    // TODO filters
  )

  class Backend(scope: BackendScope[Unit, State]) {
    // TODO Ajaxy stuff goes here
  }

  val DeploymentsScreen = ReactComponentB[Unit]("DeploymentsScreen")
    .initialState(State(Nil))
    .backend(new Backend(_))
    .render_S(state => {
      println("Rendering", state)
      <.div(state.deployments.map(dep => <.div(dep.timestamp)))
    })
    .componentDidMount(scope => Callback.future {
      Ajax.get("/deployments").map { xhr =>
        val deployments = parse(xhr.responseText).flatMap(_.as[Seq[Deployment]]).getOrElse(Nil)
        println(deployments)
        scope.setState(State(deployments))
      }
    })
    .build

  val routerConfig = RouterConfigDsl[Page].buildConfig { dsl =>
    import dsl._
    (emptyRule
    | staticRoute("/",          Home)             ~> render(<.h2("TODO"))
    | staticRoute("/#deployments", DeploymentsList)  ~> render(DeploymentsScreen())
    | staticRoute("/#api-key",     ApiKeyManagement) ~> render(<.h2("TODO api-key management"))
    ) .notFound(redirectToPage(Home)(Redirect.Replace))
      .logToConsole
  }


  @JSExport
  def main(): Unit = {
    val router = Router(BaseUrl.fromWindowOrigin, routerConfig)
    router() render document.getElementById("app")
  }

}

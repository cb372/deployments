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
import japgolly.scalajs.react.extra.router.StaticDsl.Route

@JSExport
object ReactApp {

  sealed trait Page
  case object Home extends Page
  case class DeploymentsList(queryParams: String) extends Page
  case object ApiKeyManagement extends Page

  case class State(
    deployments: Seq[Deployment],
    page: Int,
    filters: Filters
  )
  object State {
    val empty = State(deployments = Nil, page = 1, filters = Filters.empty)


  }
  case class Filters(
    appName: Option[String],
    environment: Option[String],
    status: Option[String]
  )
  object Filters {
    val empty = Filters(appName = None, environment = None, status = None)
  }

  class Backend(scope: BackendScope[DeploymentsList, State]) {
    // TODO Ajaxy stuff goes here
  }

  val DeploymentsScreen = ReactComponentB[DeploymentsList]("DeploymentsScreen")
    .initialState(State.empty)
    .backend(new Backend(_))
    .render($ => {
      println("Rendering", $.state)
      println("Query params", $.props.queryParams)
      <.div($.state.deployments.map(dep => <.div(dep.timestamp)))
    })
    .componentDidMount(scope => Callback.future {
      Ajax.get("/deployments").map { xhr =>
        val deployments = parse(xhr.responseText).flatMap(_.as[Seq[Deployment]]).getOrElse(Nil)
        println(deployments)
        scope.setState(State(deployments, 1, Filters.empty))
      }
    })
    .build

  case class Foo(app: String, env: String)

  val routerConfig = RouterConfigDsl[Page].buildConfig { dsl =>
    import dsl._

    val deploymentsList: Route[DeploymentsList] =
      ("/#deployments" ~ string("""\?.*""").option).xmap(params => DeploymentsList(params.getOrElse("")))(dl => Some(dl.queryParams))

    (emptyRule
    | staticRoute("/",          Home)             ~> render(<.h2("TODO"))
    //| staticRoute("/#deployments", DeploymentsList)  ~> render(DeploymentsScreen())
    | dynamicRouteCT(deploymentsList)  ~> dynRender(DeploymentsScreen(_))
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

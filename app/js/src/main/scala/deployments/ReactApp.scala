package deployments

import scala.scalajs._
import scala.scalajs.js.annotation.JSExport
import org.scalajs.dom._

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.prefix_<^._

@JSExport
object ReactApp {

  val DeploymentsScreen = ReactComponentB[String]("DeploymentsScreen")
    .render_P(name => <.div("Hello ", name))
    .build

  @JSExport
  def main(): Unit = {
    DeploymentsScreen("Chris") render document.getElementById("app")
  }

}

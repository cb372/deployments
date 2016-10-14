package deployments

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import scala.io.StdIn
 
object Server {

  def main(args: Array[String]) {
 
    implicit val system = ActorSystem("my-system")
    implicit val materializer = ActorMaterializer()
    // needed for the future flatMap/onComplete in the end
    implicit val executionContext = system.dispatcher
 
    import io.circe._
    import io.circe.generic.auto._
    import io.circe.syntax._

    val route =
      get {
        pathSingleSlash {
          complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, 
            """
            |<!DOCTYPE html>
            |<html>
            |  <head>
            |    <script src="/deployments-jsdeps.js"></script>
            |    <script src="/deployments-fastopt.js"></script>
            |  </head>
            |  <body>
            |    <div id="app"></div>
            |    <script>
            |      deployments.ReactApp().main();
            |    </script>
            |  </body>
            |</html>
            |""".stripMargin))
        } ~
        path ("deployments") {
          complete(HttpEntity(ContentTypes.`application/json`,
            Deployment("2016-10-14T12:00:00Z", "my app", "123", "Success").asJson.spaces2
          ))
        } ~
        getFromResourceDirectory("")
      }
 
    val bindingFuture = Http().bindAndHandle(route, "localhost", 8080)
 
    println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
    StdIn.readLine() // let it run until user presses return
    bindingFuture
      .flatMap(_.unbind()) // trigger unbinding from the port
      .onComplete(_ => system.terminate()) // and shutdown when done
  }
}

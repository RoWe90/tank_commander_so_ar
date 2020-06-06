package http

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import controller.TankModelControllerInterface
import play.api.libs.json.Json

import scala.concurrent.{ExecutionContextExecutor, Future}

class HttpServer(tankModelController: TankModelControllerInterface) {

  implicit val system: ActorSystem = ActorSystem("my-system")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  //TODO: Race condition:
  // {"which":1,"attribute":"init","value":""}
  // {"which":2,"attribute":"init","value":""}
  // {"which":2,"attribute":"posC","value":"10_5"}
  // {"which":1,"attribute":"posC","value":"0_5"}
  // Changed Tank1 posC to 0_5
  // Changed Tank1 init to
  // Changed Tank2 posC to 10_5
  // Changed Tank2 init to

  val route: Route = concat(
    get {
      path("tank" / "1" / Segment) {
        attribute => {

          var container = ""

          attribute match {
            case "tankBaseDamage" => container = tankModelController.getTankBaseDamage(1).toString
            case "hp" => container = tankModelController.getTankHp(1).toString
            case "accuracy" => container = tankModelController.getTankAccuracy(1).toString
            case "posC" => container = tankModelController.getTankPosC(1).toString()
            case "facing" => container = tankModelController.getTankFacing(1)
            case _ => println("Wrong attribute argument")
          }
          println("Tank1 " + attribute + "=" + container)
          complete(Json.toJson(container).toString())
        }
      }
    },
    get {
      path("tank" / "2" / Segment) {
        attribute => {

          var container = ""

          attribute match {
            case "tankBaseDamage" => container = tankModelController.getTankBaseDamage(2).toString
            case "hp" => container = tankModelController.getTankHp(2).toString
            case "accuracy" => container = tankModelController.getTankAccuracy(2).toString
            case "posC" => container = tankModelController.getTankPosC(2).toString()
            case "facing" => container = tankModelController.getTankFacing(2)
            case _ => println("Wrong attribute argument")
          }
          println("Tank2 " + attribute + "=" + container)
          complete(Json.toJson(container).toString())
        }
      }
    },
    post {
      path("tank") {
        decodeRequest {
          entity(as[String]) { string => {
            println(string)
            val which = (Json.parse(string) \ "which").get.toString.toInt
            val attribute = (Json.parse(string) \ "attribute").get.toString.replace("\"", "")
            val value = (Json.parse(string) \ "value").get.toString.replace("\"", "")

            if (which >= 1 && which < 3) {
              changeAttribute(which, attribute, value)
              println("Changed Tank"+ which + " " + attribute + " to " + value)
            } else {
              println("Wrong Player argument")
            }

            complete("")
          }
          }
        }
      }
    }
  )

  val bindingFuture: Future[Http.ServerBinding] = Http().bindAndHandle(route, "localhost", 54252)


  def unbind(): Unit = {
    bindingFuture
      .flatMap(_.unbind())
      .onComplete(_ => system.terminate())
  }

  def changeAttribute(tank: Int, attribute: String, value: String): Unit = {
    attribute match {
      case "init" => tankModelController.initTank(tank)
      case "tankBaseDamage" => tankModelController.setTankBaseDamage(tank.toInt, value.toInt)
      case "hp" => tankModelController.setTankHp(tank.toInt, value.toInt)
      case "accuracy" => tankModelController.setTankAccuracy(tank.toInt, value.toInt)
      case "posC" => {
        val tuple = value.split("_")
        tankModelController.setTankPosC(tank.toInt, (tuple(0).toInt, tuple(1).toInt))
      }
      case "facing" => tankModelController.setTankFacing(tank.toInt, value)
      case _ => println("Wrong Attribute Argument: " + attribute)
    }
  }



}
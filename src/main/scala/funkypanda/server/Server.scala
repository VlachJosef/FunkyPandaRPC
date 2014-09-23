package funkypanda.server

import unfiltered.request._
import unfiltered.response._
import unfiltered.directives._
import unfiltered.directives.Directives._
import funkypanda.service.UserService
import funkypanda.Settings
import com.typesafe.scalalogging.LazyLogging

class UserRPC extends unfiltered.filter.Plan with LazyLogging {

  def intent = Directive.Intent {

    case GET(Path("/")) => {
      success(Ok ~> ResponseString("FunkyPanda RPC server"))
    }

    /* Only for testing purposes */
    case GET(Path("/flushDB")) => {
      val response = UserService.flushDB
      success(Ok ~> ResponseString(response))
    }

    case req @ POST(Path("/saveUser")) => {
      logger.debug("saveUser>>")
      val userObject = Body.bytes(req)
      val response = UserService.saveUser(userObject)
      logger.debug("saveUser<<")
      returnProtoBuf(response)
    }

    case req @ POST(Path("/findUser")) => {
      logger.debug("findUser>>")
      val userId = Body.bytes(req)
      val response = UserService.findUser(userId)
      logger.debug("findUser<<")
      returnProtoBuf(response)
    }
  }

  private def returnProtoBuf(response: Array[Byte]) = {
    success(Ok ~>
      ResponseHeader("Content-Type", Set("application/octet-stream")) ~>
      ResponseHeader("Access-Control-Allow-Origin", Set("*")) ~>
      ResponseHeader("Access-Control-Allow-Methods", Set("GET, POST, OPTIONS, PUT, PATCH, DELETE")) ~>
      ResponseBytes(response))
  }
}

/** embedded server */
object Server {
  def main(args: Array[String]) {
    unfiltered.jetty.Server.http(port = Settings.server.port).plan(new UserRPC).run
  }
}

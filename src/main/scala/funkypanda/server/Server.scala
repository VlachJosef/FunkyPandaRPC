package funkypanda.server

import unfiltered.request._
import unfiltered.response._
import unfiltered.directives._
import unfiltered.directives.Directives._
import funkypanda.service.UserService

class UserRPC extends unfiltered.filter.Plan {

  def intent = Directive.Intent {

    case GET(Path("/flushDB")) => {
      val response = UserService.flushDB
      success(Ok ~> ResponseString(response))
    }

    case req @ POST(Path("/saveUser")) => {
      val userObject = Body.bytes(req)
      val response = UserService.saveUser(userObject)
      success(Ok ~> ResponseHeader("Content-Type", Set("application/octet-stream")) ~> ResponseBytes(response))
    }

    case req @ POST(Path("/findUser")) => {
      val userId = Body.bytes(req)
      val response = UserService.findUser(userId)
      success(Ok ~> ResponseHeader("Content-Type", Set("application/octet-stream")) ~> ResponseBytes(response))
    }
  }
}

/** embedded server */
object Server {
  def main(args: Array[String]) {
    unfiltered.jetty.Server.local(8080).plan(new UserRPC).run
  }
}

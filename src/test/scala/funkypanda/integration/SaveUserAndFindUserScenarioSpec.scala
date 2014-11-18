package funkypanda.integration

import org.scalatest.FlatSpec
import org.scalatest.Matchers
import dispatch.Defaults.executor
import dispatch.Http
import dispatch.as
import dispatch.implyRequestHandlerTuple
import dispatch.url
import funkypanda.proto.ServerStatusProtos.ServerStatus
import funkypanda.proto.UserObjectProtos.UserObject
import funkypanda.proto.UserIdProtos.UserId
import funkypanda.proto.UserDataResponseProtos.UserDataResponse
import funkypanda.Settings
import org.scalatest.concurrent.ScalaFutures

class SaveUserAndFindUserScenario extends FlatSpec with Matchers with ScalaFutures {

  val userObject = UserObject.newBuilder()
    .setUserName("vlach")
    .setBirthYear(1981)

  val userId = UserId.newBuilder().setUserID(1)

  "RPC saveUser and findUser" should "save and find user" in {

    val payload = userObject.build().toByteArray()
    val findUserPayload = userId.build().toByteArray()

    //val hostname = "funkypandaloadbalancer-425762762.eu-west-1.elb.amazonaws.com"
    val hostname = s"localhost:${Settings.server.port}"

    val address = s"http://$hostname"

    val flushDBReq = url(s"$address/flushDB")
    val saveUserReq = url(s"$address/saveUser")
    val findUserReq = url(s"$address/findUser")

    val flushDB = Http(flushDBReq OK as.String).futureValue
    val findUser = Http(findUserReq.POST.setBody(findUserPayload) OK as.Bytes).futureValue
    val saveUser = Http(saveUserReq.POST.setBody(payload) OK as.Bytes).futureValue
    val saveUser2 = Http(saveUserReq.POST.setBody(payload) OK as.Bytes).futureValue
    val findUser2 = Http(findUserReq.POST.setBody(findUserPayload) OK as.Bytes).futureValue

    flushDB should be("OK")

    val userDataResponse = UserDataResponse.parseFrom(findUser)

    userDataResponse.getServerStatus().getStatus() should be("Error: User with userId 1 doesn't exists")
    userDataResponse.getUserData().isInitialized() should be(false)

    val statusObj = ServerStatus.parseFrom(saveUser)
    statusObj.getStatus() should be("OK")

    val statusObj2 = ServerStatus.parseFrom(saveUser2)
    statusObj2.getStatus() should be("Error: User vlach already exists")

    val userDataResponse2 = UserDataResponse.parseFrom(findUser2)

    userDataResponse2.getServerStatus().getStatus() should be("OK")
    userDataResponse2.getUserData().isInitialized() should be(true)
    userDataResponse2.getUserData().getBirthYear() should be(1981)
    userDataResponse2.getUserData().getUserName() should be("vlach")
  }
}
package funkypanda.unit

import java.io.ByteArrayInputStream
import org.scalatest.FlatSpec
import org.scalatest.Matchers
import funkypanda.proto.ServerStatusProtos.ServerStatus
import funkypanda.proto.UserObjectProtos.UserObject
import funkypanda.service.UserService
import funkypanda.proto.UserIdProtos.UserId
import funkypanda.proto.UserDataResponseProtos.UserDataResponse
import org.scalatest.BeforeAndAfterAll

class UserServiceSpec extends FlatSpec with Matchers with BeforeAndAfterAll {

  override def beforeAll() {
    UserService.flushDB
  }

  val userObject = UserObject.newBuilder()
    .setUserName("vlach")
    .setBirthYear(1981)

  "UserService.saveUser method " should "persist user object and return server status" in {
    val response = UserService.saveUser(userObject.build().toByteArray())

    val statusObj = ServerStatus.parseFrom(new ByteArrayInputStream(response))
    statusObj.getStatus() should be("OK")
  }

  it should "return error status when user already exists" in {
    val response = UserService.saveUser(userObject.build().toByteArray())

    val statusObj = ServerStatus.parseFrom(new ByteArrayInputStream(response))
    statusObj.getStatus() should be("Error: User vlach already exists")
  }

  "UserService.findUser method " should "return server status and optional user object" in {
    val userId = UserId.newBuilder().setUserID(1)
    val response = UserService.findUser(userId.build().toByteArray())

    val userData = UserDataResponse.parseFrom(response)
    val userObj = userData.getUserData()
    val serverStatus = userData.getServerStatus().getStatus()

    serverStatus should be("OK")
    userObj.isInitialized() should be(true)
    userObj.getBirthYear() should be(1981)
    userObj.getUserName() should be("vlach")
  }
 
  it should "return error status and no user object, when user doesn't exists" in {
    val userId = UserId.newBuilder().setUserID(-1)
    val response = UserService.findUser(userId.build().toByteArray())

    val userData = UserDataResponse.parseFrom(response)
    val userObj = userData.getUserData()
    val serverStatus = userData.getServerStatus().getStatus()

    serverStatus should be("Error: User with userId -1 doesn't exists")
    userObj.isInitialized() should be(false)
  }
}
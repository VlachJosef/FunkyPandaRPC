package funkypanda.service

import org.sedis._
import redis.clients.jedis._
import org.sedis.Dress._
import funkypanda.proto.ServerStatusProtos.ServerStatus
import funkypanda.proto.UserObjectProtos.UserObject
import funkypanda.proto.UserIdProtos.UserId
import funkypanda.proto.UserDataResponseProtos.UserDataResponse

object UserService {

  val pool = new Pool(new JedisPool(new JedisPoolConfig(), "localhost", 6379, 2000))

  /**
   * For testing purposes only
   */
  def flushDB: String = {
    pool.withClient { client =>
      client.flushDB
    }
  }

  def saveUser(userObject: Array[Byte]): Array[Byte] = {
    val userObj = UserObject.parseFrom(userObject)
    val status = pool.withClient { client =>
      val userName = userObj.getUserName()
      val exists = client.exists(s"user:$userName")

      if (!exists) {
        val id = client.incr("userId")
        client.set(s"user:$id", userName)
        //client.hset(s"user:$userName", "userName", userName) // we have user name under key named user:n 
        client.hset(s"user:$userName", "birthYear", userObj.getBirthYear().toString())
        "OK"
      } else {
        s"Error: User $userName already exists"
      }
    }

    val serverStatus = ServerStatus.newBuilder().setStatus(status);
    serverStatus.build().toByteArray()
  }

  def findUser(userId: Array[Byte]): Array[Byte] = {
    val userObj = UserId.parseFrom(userId)
    val user = pool.withClient { client =>
      val id = userObj.getUserID()
      val userId = client.get(s"user:$id")
      userId match {
        case None => None
        case Some(userName) => {
          val birthYear = client.hget(s"user:$userName", "birthYear").toInt
          Some(UserObject.newBuilder().setBirthYear(birthYear).setUserName(userName).build())
        }
      }
    }

    val status = if (user.isDefined) "OK" else s"Error: User with userId ${userObj.getUserID()} doesn't exists"
      
    val serverStatus = ServerStatus.newBuilder().setStatus(status);
    val response = UserDataResponse.newBuilder().setServerStatus(serverStatus)
    if (user.isDefined) response.setUserData(user.get)
 
    response.build().toByteArray()
  }
}

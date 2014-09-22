package funkypanda

import com.typesafe.config.ConfigFactory
import java.io.File

object Settings {
  private val fileConfig = ConfigFactory.parseFile(new File("config/application.conf"));
  private val config = ConfigFactory.load(fileConfig)

  object server {
    lazy val port = config.getInt("server.port")
  }

  object redis {
    lazy val host = config.getString("redis.host")
    lazy val port = config.getInt("redis.port")
  }
}
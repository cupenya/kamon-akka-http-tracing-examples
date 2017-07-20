package org.elmarweber.github.kate.auth

import com.typesafe.config.ConfigFactory

object Configuration {
  private val rootConfig = ConfigFactory.load()

  object service {
    object http {
      private val config = rootConfig.getConfig("service.http")

      val interface = config.getString("interface")
      val port = config.getInt("port")
    }
  }

}

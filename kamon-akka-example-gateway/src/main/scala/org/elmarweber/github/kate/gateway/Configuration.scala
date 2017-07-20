package org.elmarweber.github.kate.gateway

import akka.http.scaladsl.model.Uri
import com.typesafe.config.ConfigFactory

object Configuration {
  private val rootConfig = ConfigFactory.load()

  object service {
    object http {
      private val config = rootConfig.getConfig("service.http")

      val interface = config.getString("interface")
      val port = config.getInt("port")
    }

    object coolStuff {
      private val config = rootConfig.getConfig("service.cool-stuff")

      val endpoint = Uri(config.getString("endpoint"))
    }

    object auth {
      private val config = rootConfig.getConfig("service.auth")

      val endpoint = Uri(config.getString("endpoint"))
    }
  }

}

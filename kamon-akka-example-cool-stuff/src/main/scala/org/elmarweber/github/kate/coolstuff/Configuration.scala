package org.elmarweber.github.kate.coolstuff

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

    object profile {
      private val config = rootConfig.getConfig("service.profile")

      val endpoint = Uri(config.getString("endpoint"))
    }

    object analyticsPipeline {
      private val config = rootConfig.getConfig("service.analytics-pipeline")

      val endpoint = Uri(config.getString("endpoint"))
    }
  }
}

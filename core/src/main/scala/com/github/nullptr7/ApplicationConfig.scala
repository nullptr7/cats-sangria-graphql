package com.github.nullptr7

import com.typesafe.config.ConfigFactory

trait ApplicationConfig {

  private[this] val config = ConfigFactory.load()

  val driver  : String = config.getString("postgres.driver")
  val url     : String = config.getString("postgres.url")
  val username: String = config.getString("postgres.username")
  val password: String = config.getString("postgres.password")

}
